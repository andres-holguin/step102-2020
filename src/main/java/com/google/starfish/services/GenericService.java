package com.google.starfish.services;

import java.sql.ResultSet;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;

enum Table {
  USERS("users"), NOTES("notes"), LABELS("labels"), FAVORITE_NOTES("favorite_notes"), MISC_LABELS("misc_note_labels");

  private String sqlTable;

  public String getSqlTable() {
    return this.sqlTable;
  }

  private Table(String sqlTable) {
    this.sqlTable = sqlTable;
  }
}

public class GenericService {
  public ResultSet getById(DataSource pool, long id, Table sqlTable) throws SQLException {
    try (Connection conn = pool.getConnection()) {
      String stmt = 
          "SELECT * "
        + "FROM starfish." + sqlTable + " "
        + "WHERE `id`= ? "
        + "LIMIT 1;";
      try (PreparedStatement getStmt = conn.prepareStatement(stmt)) {
        getStmt.setLong(1, id);
        ResultSet rs = getStmt.executeQuery();
        return rs;
      } 
    }
  }

  public boolean deleteById(DataSource pool, long id, Table sqlTable) throws SQLException {
    try (Connection conn = pool.getConnection()) {
      String stmt = 
          "DELETE * "
        + "FROM starfish." + sqlTable + " "
        + "WHERE `id`= ? "
        + "LIMIT 1;";
      try (PreparedStatement deleteStmt = conn.prepareStatement(stmt)) {
        deleteStmt.setLong(1, id);
        return deleteStmt.execute();
      } 
    }

  }
}
