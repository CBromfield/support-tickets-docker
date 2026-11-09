package edu.seminolestate.tickets.repository;
import edu.seminolestate.tickets.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;
import java.sql.*; import java.util.*;
@Repository
public class TicketRepository {
 private final JdbcTemplate jdbc;
 public TicketRepository(JdbcTemplate jdbc){this.jdbc=jdbc;}
 private Ticket map(ResultSet rs,int n)throws SQLException{return new Ticket(rs.getLong("id"),rs.getString("requester_name"),rs.getString("email"),rs.getString("category"),rs.getString("description"),TicketStatus.valueOf(rs.getString("status")),rs.getString("access_token"));}
 public List<Ticket> findAll(){return jdbc.query("select * from tickets order by id",this::map);}
 public Optional<Ticket> findById(long id){return jdbc.query("select * from tickets where id=?",this::map,id).stream().findFirst();}
 // Intentionally simple starter implementation for students to assess.
 public List<Ticket> search(String term) {
	    String sql = """
	        SELECT *
	        FROM tickets
	        WHERE description ILIKE ? ESCAPE '\\'
	           OR requester_name ILIKE ? ESCAPE '\\'
	        ORDER BY id
	        """;

	    String escaped = term.replace("\\", "\\\\")
	                         .replace("%", "\\%")
	                         .replace("_", "\\_");

	    String pattern = "%" + escaped + "%";

	    return jdbc.query(sql, this::map, pattern, pattern);
	}
 public Ticket save(String name,String email,String category,String description,String token){
  var kh=new GeneratedKeyHolder();
  PreparedStatementCreator psc=c->{PreparedStatement p=c.prepareStatement("insert into tickets(requester_name,email,category,description,status,access_token) values(?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);p.setString(1,name);p.setString(2,email);p.setString(3,category);p.setString(4,description);p.setString(5,"OPEN");p.setString(6,token);return p;};
  jdbc.update(psc,kh); return findById(kh.getKey().longValue()).orElseThrow();
 }
 public void updateStatus(long id,TicketStatus status){jdbc.update("update tickets set status=? where id=?",status.name(),id);}
}
