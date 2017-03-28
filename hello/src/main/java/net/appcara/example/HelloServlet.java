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
         out.println("<head><title>DevOps Demo</title>");
         out.println("<style> table { font-family: arial, sans-serif; border-collapse: collapse; width: 100%; }");
         out.println("td, th { border: 1px solid #dddddd; text-align: left; padding: 8px; }");
         out.println("tr:nth-child(even) { background-color: #dddddd; } </style>");
         out.println("</head>");
         out.println("<body>");
         out.println("<h1>Employee List</h1>");
 
         out.println("<table>");
         out.println("<tr>");
         out.println("<th>First Name</th>");
         out.println("<th>Last Name</th>");
         out.println("</tr>");

         conn = pool.getConnection();
         stmt = conn.createStatement();
         ResultSet rset = stmt.executeQuery("SELECT first_name, last_name FROM employees");
         int count=0;
         while(rset.next()) {
            out.println("<tr>");
            out.println("<td>" + rset.getString("first_name") + "</td>");
            out.println("<td>" + rset.getString("last_name") + "</td>");
            out.println("</tr>");
            ++count;
         }

         out.println("</table>");

         out.println("<p>==== " + count + " rows found =====</p>");

         out.println("<h1>Cloud Connect demo - March 28 14:04</h1>");

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
    }
}
