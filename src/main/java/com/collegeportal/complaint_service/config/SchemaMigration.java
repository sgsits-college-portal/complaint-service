package com.collegeportal.complaint_service.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Handles schema-level migrations that Hibernate ddl-auto cannot express
 * safely (e.g. adding columns to existing tables without dropping data).
 */
@Configuration
public class SchemaMigration {

    private static final Logger log = LoggerFactory.getLogger(SchemaMigration.class);

    private final DataSource dataSource;

    public SchemaMigration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void migrate() {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            if (!columnExists(meta, "complaints", "raised_by_role")) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE complaints ADD COLUMN raised_by_role VARCHAR(50)");
                    log.info("SchemaMigration: added column raised_by_role to complaints");
                }
            }
        } catch (Exception ex) {
            log.warn("SchemaMigration skipped or failed: {}", ex.getMessage());
        }
    }

    private boolean columnExists(DatabaseMetaData meta, String table, String column) throws Exception {
        try (ResultSet rs = meta.getColumns(null, null, table, column)) {
            return rs.next();
        }
    }
}
