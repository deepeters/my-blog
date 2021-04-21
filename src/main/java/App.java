import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import models.Post;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        staticFileLocation("/public");

        port(getHerokuAssignedPort());
        staticFileLocation("/public");

        //get: show new post form
        get("/posts/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "post-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process new post form
        post("/posts/new", (request, response) -> { //URL to make new post on POST route
            Map<String, Object> model = new HashMap<>();

            String content = request.queryParams("content");
            Post newPost = new Post(content);
            model.put("post", newPost);
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show all posts
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            ArrayList<Post> posts = Post.getAll();
            model.put("posts", posts);

            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //get: show an individual post
        get("/posts/:id", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfPostToFind = Integer.parseInt(req.params(":id")); //pull id - must match route segment
            Post foundPost = Post.findById(idOfPostToFind); //use it to find post
            model.put("post", foundPost); //add it to model for template to display
            return new ModelAndView(model, "post-detail.hbs"); //individual post page.
        }, new HandlebarsTemplateEngine());

        //get: show a form to update a post
        get("/posts/:id/update", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfPostToEdit = Integer.parseInt(req.params("id"));
            Post editPost = Post.findById(idOfPostToEdit);
            model.put("editPost", editPost);
            return new ModelAndView(model, "post-form.hbs");
        }, new HandlebarsTemplateEngine());

        //post: process a form to update a post
        post("/posts/:id/update", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String newContent = req.queryParams("content");
            int idOfPostToEdit = Integer.parseInt(req.params("id"));
            Post editPost = Post.findById(idOfPostToEdit);
            editPost.update(newContent); //don’t forget me
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get: delete an individual post
        get("/posts/:id/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            int idOfPostToDelete = Integer.parseInt(req.params("id")); //pull id - must match route segment
            Post deletePost = Post.findById(idOfPostToDelete); //use it to find post
            deletePost.deletePost();
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());

        //get: delete all posts
        get("/posts/delete", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            Post.clearAllPosts();
            return new ModelAndView(model, "success.hbs");
        }, new HandlebarsTemplateEngine());
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
        }
}
