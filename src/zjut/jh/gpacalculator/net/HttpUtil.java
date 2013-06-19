package zjut.jh.gpacalculator.net;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.HttpClient;

public class HttpUtil {

	private String userId;
	private String password;
	private String term;
	private Cookie[] user_cookie;
	private HttpClient loginClient, queryClient, viewStateClient;
	private PostMethod loginPost, queryPost;
	private GetMethod viewStateGet;

	public HttpUtil(String userId, String password, String term) {
		this.userId = userId;
		this.password = password;
		this.term = term;
	}

	/*
	 * 登入功能的post
	 */
	public boolean login() throws Exception {
		// 登入功能的post
		loginClient = new HttpClient();
		loginPost = new PostMethod(NetConstant.URL_LOGIN);

		loginPost.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gb2312");
		loginPost.addParameter("__EVENTTARGET", "");
		loginPost.addParameter("__EVENTARGUMENT", "");
		loginPost.addParameter("__VIEWSTATE", NetConstant.LOGIN_ConstantParam);
		loginPost.addParameter("Cbo_LX", "学生");
		loginPost.addParameter("Txt_UserName", userId);
		loginPost.addParameter("Txt_Password", password);
		loginPost.addParameter("Img_DL.x", "40");
		loginPost.addParameter("Img_DL.y", "13");

		int loginStatusCode = loginClient.executeMethod(loginPost);
		//System.out.println("aaa=" + loginStatusCode);

		// 得到用户cookie信息
		user_cookie = loginClient.getState().getCookies();
		if (user_cookie.length != 0 && loginStatusCode == 302) {
			/*for (int i = 0; i < user_cookie.length; i++) {
				System.out.println("cookies[" + i + "]="+ user_cookie[i].toString());
			}*/
			loginPost.releaseConnection();
			return true;
		} else {
			// System.out.println("dddd","登入失败，未获取cookies");
			return false;
		}
	}

	public String getViewStateHtml() throws Exception {

		viewStateClient = new HttpClient();
		viewStateGet = new GetMethod(NetConstant.URL_GetViewState);

		viewStateGet.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=gb2312");
		viewStateGet.setRequestHeader("Cookie", user_cookie[0].toString());

		int viewStateGetCode = viewStateClient.executeMethod(viewStateGet);

		String viewStateResponse = new String(viewStateGet
				.getResponseBodyAsString().getBytes("UTF-8"));
		// System.out.println("viewStateResponse="+viewStateResponse);
		viewStateGet.releaseConnection();

		return viewStateResponse;

	}

	/*
	 * 查询功能的post
	 */
	public String query(String viewState) throws Exception {
		// 查询功能的post
		queryClient = new HttpClient();
		queryPost = new PostMethod(NetConstant.URL_CJCX);

		queryPost.setRequestHeader("Content-Type",
				"application/x-www-form-urlencoded;charset=gb2312");
		queryPost.addParameter("__EVENTTARGET", "");
		queryPost.addParameter("__EVENTARGUMENT", "");
		queryPost.addParameter("__VIEWSTATE", viewState);
		queryPost.addParameter("1", "rbtnXq");
		queryPost.addParameter("ddlXq", term);
		queryPost.addParameter("ddlKc", "＝所有课程＝");
		queryPost.addParameter("Button1", "普通考试成绩查询");
		queryPost.setRequestHeader("Cookie", user_cookie[0].toString());

		int queryStatusCode = queryClient.executeMethod(queryPost);
		//System.out.println("1111=" + queryStatusCode);

		// 得到返回页面并打印
		String queryResponse = new String(queryPost.getResponseBodyAsString()
				.getBytes("UTF-8"));
		// System.out.println("queryResponse="+queryResponse);
		queryPost.releaseConnection();
		return queryResponse;
	}

}
