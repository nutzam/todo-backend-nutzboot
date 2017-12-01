package io.nutz.todomvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.mvc.view.HttpStatusView;

@IocBean
@Filters(@By(type=CrossOriginFilter.class))
@Ok("json:full")
public class TodoLauncher {
	
    Map<String, Todo> todos = new HashMap<String, Todo>();

    int currentId;

    @GET
    @At("/")
    public List<Todo> getAll() {
        return new ArrayList<Todo>(todos.values());
    }

    @GET
    @At("/?")
    public Object getTodoById(String id) {
    	Todo todo = todos.get(id);
    	if (todo == null)
    		return new HttpStatusView(404);
        return todo;
    }

    @POST
    @At("/")
    @AdaptBy(type=JsonAdaptor.class)
    public Todo createTodo(Todo todo, HttpServletRequest request) {
        todo.setUrl(request.getRequestURL().toString() + currentId);
        todos.put(""+currentId, todo);
        currentId++;
        return todo;
    }

    @At(value="/?", methods="patch")
    @AdaptBy(type=JsonAdaptor.class)
    public Object update(String id, Todo todo) {
    	Todo _todo = todos.get(id);
    	if (_todo == null)
    		return new HttpStatusView(404);
        return _todo.patchWith(todo);
    }

    @DELETE
    @At("/")
    public void deleteAll() {
        todos.clear();
    }

    @DELETE
    @At("/?")
    public void deleteTodo(String id) {
        todos.remove(id);
    }
    
    @At(value= {"/", "/?"}, methods="OPTIONS")
    public void nop() {}

    public static void main(String[] args) throws Exception {
        new NbApp(TodoLauncher.class).run();
    }

}
