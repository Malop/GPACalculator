package zjut.jh.gpacalculator.net;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import zjut.jh.gpacalculator.Bean.ScoreBean;
import zjut.jh.gpacalculator.Bean.StudentInfoBean;

public class HtmlPaser {

	private Document doc;
	
	public HtmlPaser(String htmlStr){
		doc = Jsoup.parse(htmlStr);
	}
	
	public StudentInfoBean getStudentInfo(){
		
		StudentInfoBean std = new StudentInfoBean();
		Element stdId = doc.select("span#lblXh").first();
		Element stdName = doc.select("span#lblXm").first();
		Element stdClass = doc.select("span#lblBjmc").first();
		
		std.setStudentId(stdId.text().toString());
		std.setStudentName(stdName.text().toString());
		std.setStudentClass(stdClass.text().toString());
		return std;
	}
	
	public String getQueryViewState(){
		String viewState=null;
		Elements eles_input = doc.getElementsByTag("input");
		for(int i=0;i<eles_input.size();i++){
			Element ele_viewState = eles_input.get(i);
			System.out.println("222="+ele_viewState.attr("name"));
			System.out.println("333="+ele_viewState.val());
			if(ele_viewState.attr("name").equals("__VIEWSTATE")){
				viewState = ele_viewState.val(); 
				break;
			}
		}
		return viewState;
	}
	
	public List<ScoreBean> getScore(){
		
		List<ScoreBean> scoreList = new ArrayList<ScoreBean>();
		
		Element	 table = doc.select("table#DataGrid1").first();
		Elements trs = table.select("tr");
		for(int i=1;i<trs.size();i++){
			ScoreBean scorebean = new ScoreBean();
			Elements td = trs.get(i).select("td");
			Element elecourse = td.get(1);
			Element eleScore = td.get(3);
			Element eleCredit = td.get(5);		
			scorebean.setCourse(elecourse.text().toString());
			scorebean.setCredit(eleCredit.text().toString());
			scorebean.setScore(eleScore.text().toString());
			scoreList.add(scorebean);
		}
		return scoreList;
	}
}
