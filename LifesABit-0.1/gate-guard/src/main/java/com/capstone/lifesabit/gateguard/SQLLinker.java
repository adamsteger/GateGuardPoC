package com.capstone.lifesabit.gateguard;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.capstone.lifesabit.gateguard.login.Member;
import com.capstone.lifesabit.gateguard.login.Member.MemberType;
import com.capstone.lifesabit.gateguard.passes.Pass;
import com.capstone.lifesabit.gateguard.notifications.Notification;
import com.capstone.lifesabit.gateguard.notifications.NotificationType;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@ComponentScan(basePackages = "com.capstone.lifesabit.gateguard")
@Component
public class SQLLinker implements DisposableBean {
    private ComboPooledDataSource cpds = new ComboPooledDataSource();
    private static SQLLinker linker;

    private static final String SQL_CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS UserTable"
            + "("
            + "user_id VARCHAR(36) PRIMARY KEY,"
            + "username varchar(32) NOT NULL,"
            + "hashed_password VARCHAR(256),"
            + "first_name VARCHAR(32) NOT NULL,"
            + "last_name VARCHAR(32) NOT NULL,"
            + "phone varchar(13) NOT NULL,"
            + "email VARCHAR(64) NOT NULL,"
            + "organization_id INT NOT NULL,"
            + "user_type VARCHAR(16) NOT NULL"
            + ")";

    private static final String SQL_CREATE_PASS_TABLE = "CREATE TABLE IF NOT EXISTS PassTable"
            + "("
            + "pass_id VARCHAR(36) PRIMARY KEY,"
            + "user_id VARCHAR(36),"
            + "usage_based boolean NOT NULL,"
            + "first_name VARCHAR(32) NOT NULL,"
            + "last_name varchar(32) NOT NULL,"
            + "email varchar(64) NOT NULL,"
            + "expired boolean NOT NULL,"
            + "expiration_date BIGINT,"
            + "uses_left int,"
            + "uses_total int"
            // +"CONSTRAINT fk_user"
            // +"FOREIGN KEY(user_id)"
            // +"REFERENCES UserTable(user_id)"
            + ")";

    private static final String SQL_CREATE_USER_SETTINGS_TABLE = "CREATE TABLE IF NOT EXISTS UserSettingsTable"
            + "("
            + "user_id varchar(36) PRIMARY KEY,"
            + "EXPIRATION_FROM_USES BOOLEAN NOT NULL,"
            + "EXPIRATION_FROM_DATE BOOLEAN NOT NULL,"
            + "MAX_EXPIRATION_USES INT NOT NULL DEFAULT '99',"
            + "MIN_EXPIRATION_USES INT NOT NULL DEFAULT '1',"
            + "MAX_EXPIRATION_DAYS INT NOT NULL DEFAULT '60',"
            + "MIN_EXPIRATION_DAYS INT NOT NULL DEFAULT '1',"
            + "NOTIFICATIONS_MARKETING BOOLEAN NOT NULL DEFAULT 'true',"
            + "NOTIFICATIONS_PASS_USAGE BOOLEAN NOT NULL DEFAULT 'true',"
            + "NOTIFICATIONS_PASS_EXPIRATION BOOLEAN NOT NULL DEFAULT 'true',"
            + "NOTIFICATIONS_ACCOUNT_SIGN_IN BOOLEAN NOT NULL DEFAULT 'true',"
            + "LIGHT_MODE BOOLEAN NOT NULL DEFAULT 'false',"
            + "DARK_MODE BOOLEAN NOT NULL DEFAULT 'true'"
            + ")";

    private static final String SQL_CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE IF NOT EXISTS NotificationsTable"
            + "("
            + "notification_id VARCHAR(36) PRIMARY KEY,"
            + "TITLE varchar(32) NOT NULL,"
            + "TYPE varchar(32) NOT NULL,"
            + "DESCRIPTION TEXT NOT NULL,"
            + "TIMESTAMP INT NOT NULL,"
            + "pass_id VARCHAR(36) NOT NULL,"
            + "user_id VARCHAR(36) NOT NULL"
            + ")";

    public static void main(String[] args) {
        SQLLinker sqlLinker = new SQLLinker();
        sqlLinker.createPassTable();
        // sqlLinker.addPass(UUID.randomUUID(), UUID.randomUUID(), "Adam", "Steger",
        // "adamsteg24@gmail.com", false, true,
        // 1, 3);
        // sqlLinker.addPass(UUID.randomUUID(), UUID.randomUUID(), "Vaughn", "Eugenio",
        // "veugenio@gmail.com", false, false,
        // Date.valueOf("2022-01-01").getTime());
        // Member member = new
        // Member(UUID.fromString("15adee9a-cfbb-4d67-89b0-5d185ccbe764"),"adamsteger",
        // "Adam", "Steger", "(803)730-3278", "adamsteg24@gmail.com",
        // "hashedpasswordhere", MemberType.USER);
        // Pass pass2 = new Pass("Adam", "Steger", "adamsteg24@gmail.com",
        // UUID.fromString("15adee9a-cfbb-4d67-89b0-5d185ccbe764"),
        // Date.valueOf("2022-01-01"));
        // Pass pass = new Pass("Adam", "Steger", "adamsteg24@gmail.com",
        // UUID.fromString("15adee9a-cfbb-4d67-89b0-5d185ccbe764"), 4,5);
        // System.out.println(pass2);
        // java.sql.Date sqlDate = new
        // java.sql.Date(pass2.getExpirationDate().getTime());
        // sqlLinker.addPass(pass2.getPassID(), pass2.getUserID(), pass2.getFirstName(),
        // pass2.getLastName(), pass2.getEmail(), pass2.getExpired(),
        // pass2.getUsageBased(), sqlDate);
        // sqlLinker.addPass(pass);
        // sqlLinker.addPass(pass2);
        // ArrayList<Pass> passes = sqlLinker.loadPasses(member);
        // for(int i = 0; i < passes.size(); i++) {
        // System.out.println(passes.get(i));
        // }

        // System.out.println("Now editing a pass");
        // sqlLinker.editPass(UUID.fromString("237e1e66-8ebe-40dc-851b-760dc6f580e2"),"Vaughn",
        // "Eugenio", "veugenio@gmail.com", false, false, Date.valueOf("2022-01-01"));
        // ArrayList<Pass> passes2 = sqlLinker.loadPasses(member);
        // for(int i = 0; i < passes2.size(); i++) {
        // System.out.println(passes2.get(i));
        // }
    }

    @Bean
    public SQLLinker start() {
        linker = new SQLLinker();
        linker.createAllTables();
        return linker;
    }

    public SQLLinker() {
        try {
            cpds.setDriverClass("org.postgresql.Driver");
            cpds.setJdbcUrl("jdbc:postgresql://localhost:5432/GateGuard");
            cpds.setUser("postgres");
            cpds.setPassword("password1234");
            cpds.setUnreturnedConnectionTimeout(10000);
            cpds.setAcquireRetryAttempts(5);
            cpds.setAcquireRetryDelay(1000);
        } catch (PropertyVetoException e) {
        e.printStackTrace();
        // handle the exception
        }
    }

    public void createAllTables() {
        this.createUserTable();
        this.createUserSettingsTable();
        this.createNotificationsTable();
        this.createPassTable();
    }

    public void createUserTable() {
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement(SQL_CREATE_USER_TABLE);
            st.execute();
            st.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createUserSettingsTable() {
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement(SQL_CREATE_USER_SETTINGS_TABLE);
            st.execute();
            st.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPassTable() {
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement(SQL_CREATE_PASS_TABLE);
            st.execute();
            st.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createNotificationsTable() {
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement(SQL_CREATE_NOTIFICATIONS_TABLE);
            st.execute();
            st.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Member getMember(String username) {
        Member member = null;
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("select * from UserTable where username = ?");
            st.setString(1, username);
            ResultSet r1 = st.executeQuery();
            if (r1.next()) {
                member = new Member(UUID.fromString(r1.getString("user_id")),
                        r1.getString("username"),
                        r1.getString("first_name"),
                        r1.getString("last_name"),
                        r1.getString("phone"),
                        r1.getString("email"),
                        r1.getString("hashed_password"),
                        "admin".equalsIgnoreCase(r1.getString("user_type")) ? MemberType.ADMIN : MemberType.USER);
            }
            st.close();
            r1.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return member;
    }

    public ArrayList<Member> getMembers() {
        ArrayList<Member> ret = new ArrayList<>();
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("select * from UserTable;");
            ResultSet r1 = st.executeQuery();
            while (r1.next()) {
                Member member = new Member(UUID.fromString(r1.getString("user_id")),
                                           r1.getString("username"),
                                           r1.getString("first_name"),
                                           r1.getString("last_name"),
                                           r1.getString("phone"),
                                           r1.getString("email"),
                                           r1.getString("hashed_password"),
                                           // Later, this will need to be the user's type.
                                           // for now, we'll assume they're a normal user
                                           MemberType.USER);
                ret.add(member);
            }
            st.close();
            r1.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    
    public ArrayList<Member> loadMembers(int organizationID) {
        ArrayList<Member> members = new ArrayList<>();
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * from UserTable where organization_id = ?");
            st.setInt(1, organizationID);
            ResultSet r1 = st.executeQuery();
            while (r1.next()) {
                Member member = new Member(UUID.fromString(r1.getString("user_id")),r1.getString("username"), r1.getString("first_name"), r1.getString("last_name"), r1.getString("phone"), r1.getString("email"), r1.getString("hashed_password"), MemberType.valueOf(r1.getString("user_type")));
                members.add(member);
            }
            st.close();
            r1.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return members;
    }

    public boolean addUser(String uuid, String username, String firstName, String lastName, String phoneNumber,
            String email, String hashedPassword, int organizationID, MemberType userType) {
        boolean addSuccessful = false;

        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO UserTable (user_id, username, hashed_password, first_name, last_name, phone, email, organization_id, user_type) VALUES (?,?,?,?,?,?,?,?,?)");
            st.setString(1, uuid);
            st.setString(2, username);
            st.setString(3, hashedPassword);
            st.setString(4, firstName);
            st.setString(5, lastName);
            st.setString(6, phoneNumber);
            st.setString(7, email);
            st.setInt(8, organizationID);
            st.setString(9, (userType == MemberType.ADMIN) ? "admin" : "user");
            st.executeUpdate();
            addSuccessful = true;
            st.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return addSuccessful;
    }

    public boolean deleteUser(UUID userID) {
        boolean deleteSuccessful = false;
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("DELETE FROM UserTable where user_id = ?");
            st.setString(1, userID.toString());
            st.executeUpdate();
            deleteSuccessful = true;
            st.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteSuccessful;
    }

    public boolean addPass(Pass pass) {
        boolean addSuccessful = false;
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("INSERT INTO PassTable (pass_id, user_id, usage_based, first_name, last_name, email, expired, expiration_date, uses_left, uses_total) VALUES (?,?,?,?,?,?,?,?,?,?)");
            st.setString(1, pass.getPassID().toString());
            st.setString(2, pass.getUserID().toString());
            st.setBoolean(3, pass.getUsageBased());
            st.setString(4, pass.getFirstName());
            st.setString(5, pass.getLastName());
            st.setString(6, pass.getEmail());
            st.setBoolean(7, pass.getExpired());
            st.setLong(8, pass.getUsageBased() ? -1 : pass.getExpirationDate());
            st.setInt(9, pass.getUsageBased() ? pass.getUsesLeft() : -1);
            st.setInt(10, pass.getUsageBased() ? pass.getUsesTotal() : -1);
            st.executeUpdate();
            addSuccessful = true;
            st.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return addSuccessful;
    }

    public boolean updatePass(UUID passID, Pass pass) {
        boolean addSuccessful = false;
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("UPDATE PassTable SET pass_id=?, user_id=?, usage_based=?, first_name=?, last_name=?, email=?, expired=?, expiration_date=?, uses_left=?, uses_total=? WHERE pass_id=?");
            st.setString(1, pass.getPassID().toString());
            st.setString(2, pass.getUserID().toString());
            st.setBoolean(3, pass.getUsageBased());
            st.setString(4, pass.getFirstName());
            st.setString(5, pass.getLastName());
            st.setString(6, pass.getEmail());
            st.setBoolean(7, pass.getExpired());
            st.setLong(8, pass.getUsageBased() ? -1 : pass.getExpirationDate());
            st.setInt(9, pass.getUsageBased() ? pass.getUsesLeft() : -1);
            st.setInt(10, pass.getUsageBased() ? pass.getUsesTotal() : -1);
            st.setString(11, pass.getPassID().toString());
            st.executeUpdate();
            addSuccessful = true;
            st.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return addSuccessful;
    }

    public boolean deletePass(UUID passID) {
        boolean passDeleted = false;
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("DELETE FROM PassTable where pass_id = ?");
            st.setString(1, passID.toString());
            st.executeUpdate();
            passDeleted = true;
            st.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passDeleted;
    }

    public ArrayList<Pass> loadPasses(Member member) {
        ArrayList<Pass> passes = new ArrayList<Pass>();
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * from PassTable where user_id = ?");
            st.setString(1, member.getUuid().toString());
            ResultSet r1 = st.executeQuery();
            while (r1.next()) {
                boolean usageBased = r1.getBoolean("usage_based");
                if (!usageBased) {
                    Pass pass = new Pass(UUID.fromString(r1.getString("pass_id")),
                            r1.getString("first_name"),
                            r1.getString("last_name"),
                            r1.getString("email"),
                            UUID.fromString(r1.getString("user_id")),
                            r1.getLong("expiration_date"));
                    passes.add(pass);
                } else if (usageBased) {
                    Pass pass = new Pass(UUID.fromString(r1.getString("pass_id")),
                            r1.getString("first_name"),
                            r1.getString("last_name"),
                            r1.getString("email"),
                            UUID.fromString(r1.getString("user_id")),
                            r1.getInt("uses_left"),
                            r1.getInt("uses_total"));
                    passes.add(pass);
                }
            }
            st.close();
            r1.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passes;
    }

    public ArrayList<Pass> loadPasses(UUID userID) {
        ArrayList<Pass> passes = new ArrayList<Pass>();
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * from PassTable where user_id = ?");
            st.setString(1, userID.toString());
            ResultSet r1 = st.executeQuery();
            while (r1.next()) {
                boolean usageBased = r1.getBoolean("usage_based");
                if (!usageBased) {
                    Pass pass = new Pass(UUID.fromString(r1.getString("pass_id")),
                            r1.getString("first_name"),
                            r1.getString("last_name"),
                            r1.getString("email"),
                            UUID.fromString(r1.getString("user_id")),
                            r1.getLong("expiration_date"));
                    passes.add(pass);
                } else if (usageBased) {
                    Pass pass = new Pass(UUID.fromString(r1.getString("pass_id")),
                            r1.getString("first_name"),
                            r1.getString("last_name"),
                            r1.getString("email"),
                            UUID.fromString(r1.getString("user_id")),
                            r1.getInt("uses_left"),
                            r1.getInt("uses_total"));
                    passes.add(pass);
                }
            }
            st.close();
            r1.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passes;
    }

    public Pass getPass(UUID passID) {
        Pass ret = null;
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * from PassTable where pass_id = ?");
            st.setString(1, passID.toString());
            ResultSet r1 = st.executeQuery();
            if (r1.next()) {
                boolean usageBased = r1.getBoolean("usage_based");
                if (!usageBased) {
                    ret = new Pass(UUID.fromString(r1.getString("pass_id")),
                            r1.getString("first_name"),
                            r1.getString("last_name"),
                            r1.getString("email"),
                            UUID.fromString(r1.getString("user_id")),
                            r1.getLong("expiration_date"));
                } else if (usageBased) {
                    ret = new Pass(UUID.fromString(r1.getString("pass_id")),
                            r1.getString("first_name"),
                            r1.getString("last_name"),
                            r1.getString("email"),
                            UUID.fromString(r1.getString("user_id")),
                            r1.getInt("uses_left"),
                            r1.getInt("uses_total"));
                }
            }
            st.close();
            r1.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean editPass(UUID passID, String firstName, String lastName, String email, boolean expired,
            boolean usageBased, long expirationDate) {
        boolean editSuccessful = false;

        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement(
                    "UPDATE PassTable SET first_name=?, last_name=?, email=?, expired=?, usage_based=?, expiration_date=? WHERE pass_id=?");
            st.setString(1, firstName);
            st.setString(2, lastName);
            st.setString(3, email);
            st.setBoolean(4, expired);
            st.setBoolean(5, usageBased);
            st.setLong(6, expirationDate);
            st.setString(7, passID.toString());

            st.executeUpdate();
            editSuccessful = true;
            st.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return editSuccessful;
    }

    public boolean editPass(UUID passID, String firstName, String lastName, String email, boolean expired,
            boolean usageBased, int usesLeft, int usesTotal) {
        boolean editSuccessful = false;
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement(
                    "UPDATE PassTable SET first_name=?, last_name=?, email=?, expired=?, usage_based=?, uses_left=?, uses_total=? WHERE pass_id=?");
            st.setString(1, firstName);
            st.setString(2, lastName);
            st.setString(3, email);
            st.setBoolean(4, expired);
            st.setBoolean(5, usageBased);
            st.setInt(6, usesLeft);
            st.setInt(7, usesTotal);
            st.setString(8, passID.toString());

            st.executeUpdate();
            editSuccessful = true;
            st.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return editSuccessful;
    }

    public boolean deletePasses(UUID userID) {
        boolean deleteSuccessful = false;
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("DELETE FROM PassTable where user_id = ?");
            st.setString(1, userID.toString());
            st.executeUpdate();
            deleteSuccessful = true;
            st.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteSuccessful;
    }

    public boolean addUser(Member member) {
        boolean status = this.addUser(member.getUuid().toString(), member.getUsername(), member.getFirstName(),
                member.getLastName(), member.getPhoneNumber(), member.getEmail(), member.getSaltedHashedPassword(),
                member.getOrgId(), member.getType());
        return status;
    }

    public ArrayList<Notification> loadNotifications(Member member) {
        ArrayList<Notification> notifications = new ArrayList<>();

        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * from NotificationsTable WHERE user_id=?");
            st.setString(1, member.getUuid().toString());
            ResultSet r1 = st.executeQuery();
            while (r1.next()) {
                Notification notif = new Notification(UUID.fromString(r1.getString("notification_id")),
                        UUID.fromString(r1.getString("pass_id")),
                        UUID.fromString(r1.getString("user_id")), NotificationType.valueOf(r1.getString("type")),
                        r1.getString("title"), r1.getString("description"),
                        r1.getTimestamp("timestamp").toLocalDateTime());
                notifications.add(notif);
            }
            conn.close();
            r1.close();
            st.close();

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notifications;
    }

    @Override
    public void destroy() throws Exception {
        if (this.cpds != null) {
            this.cpds.close();
        }
    }

    public static SQLLinker getInstance() {
        return linker;
    }

    public Pass getPass(String passID) {
        Pass ret = null;
        try {
            Connection conn = cpds.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM PassTable WHERE pass_id=?;");
            st.setString(1, passID);
            ResultSet rs = st.executeQuery();
            // expired, usage_based, expiration_date, uses_left, uses_total
            if (rs.next()) {
                boolean usageBased = rs.getBoolean("usage_based");
                if (usageBased) {
                    ret = new Pass(UUID.fromString(rs.getString("pass_id")), rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"), UUID.fromString(rs.getString("user_id")), rs.getInt("uses_left"), rs.getInt("uses_total"));
                } else {
                    ret = new Pass(UUID.fromString(rs.getString("pass_id")), rs.getString("first_name"), rs.getString("last_name"), rs.getString("email"), UUID.fromString(rs.getString("user_id")), rs.getLong("expiration_date"));
                }
            }
            conn.close();
            rs.close();
            st.close();

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
