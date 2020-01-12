package stefanowicz.kacper.repository.connection;

import org.jdbi.v3.core.Jdbi;

public class DbConnection {
    private static DbConnection connection = new DbConnection();

    private final String URL = "jdbc:mysql://localhost:3306/cinema_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private final String USERNAME = "root";
    private final String PASSWORD = "admin";

    private final Jdbi jdbi = Jdbi.create(URL, USERNAME, PASSWORD);

    private DbConnection(){}

    public static DbConnection getConnection() {return  connection;}

    public Jdbi getJdbi() { return jdbi; }
}
