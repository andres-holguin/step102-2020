package com.google.starfish.servlets;  

import java.io.IOException;  
import java.sql.Connection;  
import java.sql.SQLException;  
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;  
import javax.servlet.annotation.WebServlet;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
import javax.sql.DataSource;
import java.util.Date;
import com.google.starfish.services.NoteService;
import com.google.starfish.models.Note;
import com.google.gson.Gson;
import java.util.ArrayList;

/** Servlet that returns search results for notes based on school and course */
@WebServlet("/search")  
public class SearchServlet extends HttpServlet {  

  private NoteService noteService = new NoteService();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    DataSource pool = (DataSource) req.getServletContext().getAttribute("my-pool");  
    String reqSchool = req.getParameter("school").toLowerCase().trim();
    String reqCourse= req.getParameter("course").toLowerCase().trim();
    System.out.println(reqSchool);
    System.out.println(reqCourse);
    ArrayList<Note> notes = new ArrayList<>();
    try (Connection conn = pool.getConnection()) {
      String stmt = getSearchQuery(reqSchool, reqCourse);
      try (PreparedStatement getNotesStatement = conn.prepareStatement(stmt)) {
        // getNotesStatement.setString(1, reqSchool);
        // getNotesStatement.setString(2, reqCourse);
        System.out.println(getNotesStatement);
        ResultSet rs = getNotesStatement.executeQuery();
        int count = 0;
        System.out.println("executed query");
        while (rs.next()) {
          System.out.println("entered while loop");

          long noteId = rs.getLong("id");
          String authorId = rs.getString("author_id");
          String school = rs.getString("school");
          String course = rs.getString("course");
          String title = rs.getString("title");
          String sourceUrl = rs.getString("source_url");
          String pdfSource = rs.getString("pdf_source");
          Date dateCreated = rs.getDate("date_created");
          long numDownloads = rs.getLong("num_downloads");
          long numFavorites = noteService.getNumFavoritesById(pool, noteId);
          System.out.println("Note id: " + noteId);
          String[] miscLabels = noteService.getMiscLabelsById(pool, noteId);

          Note thisNote = new Note.Builder()
                              .setId(noteId)
                              .setAuthorId(authorId)
                              .setRequiredLabels(school, course)
                              .setNoteTitle(title)
                              .setSourceUrl(sourceUrl)
                              .setOptionalPdfSource(pdfSource)
                              .setDateCreated(dateCreated)
                              .setNumDownloads(numDownloads)
                              .setNumFavorites(numFavorites)
                              .setMiscLabels(miscLabels)
                              .build();
          notes.add(thisNote);
          count++;
          System.out.println("Found a note.");
        }
        System.out.println(count);
        String json = convertArrayListToJson(notes);
        System.out.println("Converting to json");
        res.setContentType("application/json");
        System.out.println(json);
        res.getWriter().println(json);
      }
    } catch (SQLException ex) {
        res.getWriter().println("Error caught while speaking to database: " + ex);  
    }
  }  

  /** Dynamically generates sql query statement for note search */
  private String getSearchQuery(String school, String course) {
    String stmt = 
        "SELECT * "
      + "FROM notes "
      + "WHERE 1=1";
    if (school != null && !school.isEmpty()) {
      stmt += " AND `school`='" + school + "'";
    }
    if (course != null && !course.isEmpty()) {
      stmt += " AND `course`='"+ course + "'";
    }
    stmt += ";";
    System.out.println(stmt);
    return stmt;
  }

  /** Converts an array list to JSON */
  private String convertArrayListToJson(ArrayList<Note> notes) {
    Gson gson = new Gson();
    return gson.toJson(notes);
  }
}  
