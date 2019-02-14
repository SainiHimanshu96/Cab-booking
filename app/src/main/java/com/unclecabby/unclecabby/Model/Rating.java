package com.unclecabby.unclecabby.Model;

public class Rating {
    private String ratings;
    private String comments;

    public Rating() {
    }

    public Rating(String ratings, String comments) {
        this.ratings = ratings;
        this.comments = comments;
    }

    public String getRatings() {

        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
