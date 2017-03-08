package net.appcara.example;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import javax.sql.DataSource;
import javax.naming.*;

public class HelloServlet extends HttpServlet {

   DataSource pool;  // Database connection pool
 
   @Override
   public void init( ) throws ServletException {
      try {
         InitialContext ctx = new InitialContext();
         pool = (DataSource)ctx.lookup("java:comp/env/jdbc/TestDB");
         if (pool == null) {
            throw new ServletException("Unknown DataSource 'jdbc/TestDB'");
         }
      } catch (NamingException ex) {
         ex.printStackTrace();
      }
   }
    
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

      response.setContentType("text/html;charset=UTF-8");
      PrintWriter out = response.getWriter();
 
      Connection conn = null;
      Statement  stmt = null;
      try {
         out.println("<!DOCTYPE html>");
         out.println("<html>");
         out.println("<head><title>Qurey Servlet</title></head>");
         out.println("<body>");
 
         conn = pool.getConnection();
         stmt = conn.createStatement();
         ResultSet rset = stmt.executeQuery("SELECT first_name, last_name FROM employees");
         int count=0;
         while(rset.next()) {
            out.println("<p>" + rset.getString("first_name") + ", "
                  + rset.getString("last_name") + "</p>");
            ++count;
         }
         out.println("<p>==== " + count + " rows found =====</p>");

         out.println("<h1>Appcara Demo</h1>");

         out.println("</body></html>");
      } catch (SQLException ex) {
         ex.printStackTrace();
      } finally {
         out.close();
         try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();  // return to pool
         } catch (SQLException ex) {
             ex.printStackTrace();
         }
      }
/*
        PrintWriter writer = response.getWriter();
        writer.print("HDS Demo");
*/
    }
}
