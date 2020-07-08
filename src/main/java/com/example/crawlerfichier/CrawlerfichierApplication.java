package com.example.crawlerfichier;

/*import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrawlerfichierApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlerfichierApplication.class, args);
	}

}*/

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrawlerfichierApplication {
	public static DB db = new DB();

	public static void main(String[] args) throws SQLException, IOException {
		db.runSql2("TRUNCATE record;");
		processPage("http://demdikk.com/");
	}

	public static void processPage(String URL) throws SQLException, IOException{
		//check if the given URL is already in database
		String sql = "select * from record where url = '"+URL+"'";
		ResultSet rs = db.runSql(sql);
		if(rs.next()){

		}else{
			//store the URL to database to avoid parsing again
			sql = "INSERT INTO  `crawler`.`record` " + "(`url`) VALUES " + "(?);";
			PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, URL);
			stmt.execute();

			//get useful information
			Document doc = Jsoup.connect("http://demdikk.com/").get();

			if(doc.text().contains("research")){
				System.out.println(URL);
			}

			//get all links and recursively call the processPage method
			Elements questions = doc.select("a[href]");
			for(Element link: questions){
				if(link.attr("href").contains("demdikk.com"))
					processPage(link.attr("abs:href"));
			}
		}
	}
}
