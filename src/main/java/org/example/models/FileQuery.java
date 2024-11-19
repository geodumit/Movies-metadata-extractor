package org.example.models;

public class FileQuery {
    String table;
    String insertQuery;
    String updateQuery;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getInsertQuery() {
        return insertQuery;
    }

    public void setInsertQuery(String insertQuery) {
        this.insertQuery = insertQuery;
    }

    public String getUpdateQuery() {
        return updateQuery;
    }

    public void setUpdateQuery(String updateQuery) {
        this.updateQuery = updateQuery;
    }

    public FileQuery(String table, String insertQuery, String updateQuery) {
        this.table = table;
        this.insertQuery = insertQuery;
        this.updateQuery = updateQuery;
    }
}
