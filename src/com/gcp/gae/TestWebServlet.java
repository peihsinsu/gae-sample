package com.gcp.gae;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class TestWebServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp){
		if(req.getParameter("id") != null) {
			System.out.println("Doing Job... id=" + req.getParameter("id"));
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
	    User usr = userService.getCurrentUser();
		String user = "no user from getUserPrincipal"; 
		if(req.getUserPrincipal()!=null) 
			user = req.getUserPrincipal().getName();
		
		String act = req.getParameter("act");
		if(act != null)
		if(act.equalsIgnoreCase("post")) {
			System.out.println("Putting Post Queue Job.....");
			Queue q = QueueFactory.getDefaultQueue();
			q.add(TaskOptions.Builder.withUrl("/Test").param("id", "12341234").method(Method.GET));
		} else if(act.equalsIgnoreCase("pull")){
			System.out.println("Putting Post Queue Job.....");
			Queue q = QueueFactory.getQueue("ProcessedOrderQueue");
			q.add(TaskOptions.Builder.withMethod(Method.PULL));
		}
		
		
		MemcacheService mem = MemcacheServiceFactory.getMemcacheService();
		mem.setErrorHandler(ErrorHandlers.getLogAndContinue(Level.INFO));
		System.out.println("put key to memcache....");
		mem.put("key", "value");
		Object key = mem.get("key");
		System.out.println("Get key from memcache... " + key.toString());
		
		
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello...." + (usr!=null?usr.getNickname():"no usr from getCurrentUser") + "::" + user  );
	}
	
	public void doPull(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		if(req.getParameter("id") != null) {
			System.out.println("Doing Job... id=" + req.getParameter("id"));
		}
	}
}
