package com.google.starfish.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;  
import java.sql.ResultSet;  
import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import com.google.starfish.models.Note;

/**
 * Service class for FavoriteNotes Table
 */
public class FavoriteNoteService {

  private String FAVORITE_NOTES = Table.FAVORITE_NOTES.getSqlTable();
  private String NOTES = Table.NOTES.getSqlTable();
  private NoteService noteService = new NoteService();

  /** Gets a favorite note by the compoud id */
  public ResultSet getRowByCompoundId(DataSource pool, long noteId, String userId) throws SQLException {
    try (Connection conn = pool.getConnection()) {
      String stmt = 
          "SELECT * "
        + "FROM " + FAVORITE_NOTES + " "
        + "WHERE `note_id`= ? AND "
        + "`user_id` = ? "
        + "LIMIT 1;";
      try (PreparedStatement getStmt = conn.prepareStatement(stmt)) {
        getStmt.setLong(1, noteId);
        getStmt.setString(2, userId);
        ResultSet rs = getStmt.executeQuery();
        return rs;
      } 
    }
  }

  /** Deletes a favorite note by the compound id */
  public boolean deleteRowByCompoundId(DataSource pool, long noteId, String userId) throws SQLException {
    try (Connection conn = pool.getConnection()) {
      String stmt = 
          "DELETE * "
        + "FROM " + FAVORITE_NOTES + " "
        + "WHERE `note_id`= ? AND "
        + "`user_id` = ? "
        + "LIMIT 1;";
      try (PreparedStatement deleteStmt = conn.prepareStatement(stmt)) {
        deleteStmt.setLong(1, noteId);
        deleteStmt.setString(2, userId);
        boolean deleted = deleteStmt.execute();
        return deleted;
      } 
    }
  }

  /** Inserts a favorite note by noteId and userId */
  public void insertFavoriteNote(DataSource pool, long noteId, String userId) {
    if (userId == null) return;
    try (Connection conn = pool.getConnection()) {
      try {
        conn.setAutoCommit(false);
        String stmt =
            "INSERT INTO " + FAVORITE_NOTES + " ( "
                + "note_id,"
                + "user_id ) "
          + "VALUES ( "
                + "?,"
                + "? ); ";
        try (PreparedStatement insertStmt = conn.prepareStatement(stmt)) {
          insertStmt.setLong(1, noteId);
          insertStmt.setString(2, userId);
          insertStmt.execute();
          conn.commit();
        }
      } catch(SQLException ex) {
        if (conn != null) {
          try {
            System.err.print("Transaction is being rolled back.");
            conn.rollback();
          } catch (SQLException excep) {
            System.err.print(excep);
          }
        }
      }
    } catch (SQLException ex) {
      System.err.print(ex);
    }
  }

  /** Gets an array of note objects that have been favorited by passed user id */
  public Note[] getFavoriteNotesByUserId(DataSource pool, String userId) throws SQLException {
    List<Note> notes = new ArrayList<>();
    try (Connection conn = pool.getConnection()) {
      String stmt = 
          "SELECT  a.* "
        + "FROM "
          + NOTES + " AS a "
          + "INNER JOIN (SELECT * "
                      + "FROM " + FAVORITE_NOTES + " "
                      + "WHERE user_id=?) "
          + "as b ON a.id=b.note_id;";
      try (PreparedStatement favNotesStmt = conn.prepareStatement(stmt)) {
        favNotesStmt.setString(1, userId);
        ResultSet rs = favNotesStmt.executeQuery();
        while (rs.next()) {
          Note thisNote = noteService.constructNoteFromSqlResult(pool, rs);
          notes.add(thisNote);
        }
        rs.close();
        return notes.toArray(new Note[0]);
      }
    }
  }

  /** Gets the number of times a note has been favorited by note id */
  public long getNumFavoritesByNoteId(DataSource pool, long noteId) throws SQLException {
    try (Connection conn = pool.getConnection()) {
      String stmt = 
          "SELECT COUNT(*) AS num_favorites "
        + "FROM " + FAVORITE_NOTES + " "
        + "WHERE `note_id`=?;";
      try (PreparedStatement selectStmt = conn.prepareStatement(stmt)) {
        selectStmt.setLong(1, noteId);
        ResultSet rs = selectStmt.executeQuery();
        rs.next();
        long numFavorites = rs.getLong("num_favorites");
        rs.close();
        return numFavorites;
      }
    }
  }
}
