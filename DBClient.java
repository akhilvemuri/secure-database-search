import java.util.Scanner;
import java.util.regex.*;
import java.sql.*;
import java.math.BigInteger;

public class DBClient {
    private Paillier paillier;
    private String url;
    private String user;
    private String password;

    public DBClient() {
        // this.paillier = new Paillier(32);
        this.url = "jdbc:postgresql://localhost:5432/test";
        this.user = "postgres";
        this.password = "password";
    }

    public void encryptDB(Connection conn) throws SQLException {
        String query = "SELECT * FROM basketball";
        
        try (Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                for (int col = 2; col <= rsmd.getColumnCount(); col++) {
                    BigInteger plaintext;
                    BigInteger ciphertext;
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE basketball SET " + rsmd.getColumnName(col) + " = ? WHERE id = ?");
                    
                    if (rsmd.getColumnType(col) == Types.BIGINT) {
                        int val = rs.getInt(col);
                        plaintext = new BigInteger(Integer.toString(val));
                        ciphertext = paillier.encrypt(plaintext);
                        ps.setLong(1, Long.parseLong(ciphertext.toString()));
                    } else if (rsmd.getColumnType(col) == Types.VARCHAR) {
                        String val = rs.getString(col);
                        plaintext = new BigInteger(val.getBytes());
                        ciphertext = paillier.encrypt(plaintext);
                        ps.setString(1, ciphertext.toString());
                    }

                    ps.setInt(2, rs.getInt("id"));
                    ps.executeUpdate();
                    System.out.println(ps.toString());
                }
            }
        }

        System.out.println(paillier.getN());
        System.out.println(paillier.getG());
        System.out.println(paillier.getLambda());
        System.out.println(paillier.getMu());
    }

    public void query(Connection conn, String query) throws SQLException {
        try (Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                    String decrypted = null;
                    
                    if (rsmd.getColumnName(col).equals("id")) {
                        decrypted = Integer.toString(rs.getInt(col));
                    } else if (rsmd.getColumnType(col) == Types.BIGINT) {
                        BigInteger val = new BigInteger(Long.toString(rs.getLong(col)));
                        decrypted = paillier.decrypt(val).toString();
                    } else if (rsmd.getColumnType(col) == Types.VARCHAR) {
                        String val = rs.getString(col);
                        decrypted = new String(paillier.decrypt(new BigInteger(val)).toByteArray());
                    }
                    System.out.print(decrypted + ", ");
                }
                System.out.println();
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        DBClient dbClient = new DBClient();

        // FOR TESTING PURPOSES ONLY
        BigInteger n = new BigInteger("1687508629");
        BigInteger g = new BigInteger("1687508630");
        BigInteger lambda = new BigInteger("1687425676");
        BigInteger mu = new BigInteger("443516969");
        dbClient.paillier = new Paillier(32, n, g, lambda, mu);

        try (Connection conn = DriverManager.getConnection(dbClient.url, dbClient.user, dbClient.password)) {
            System.out.println("Connected to database!");
            
            // ONLY ENCRYPT DATABASE ONCE!!
            // dbClient.encryptDB(conn);
            // System.out.println("Finished encrypting database!");
            
            Scanner input = new Scanner(System.in);
            String query;
            while (true) {
                System.out.println();
                System.out.println("PLEASE ENTER A SQL QUERY: ");
                query = input.nextLine();
                dbClient.query(conn, query);
            }
        }
    }
}