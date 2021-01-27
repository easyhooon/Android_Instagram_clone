package kr.ac.konkuk.instagramclone.Model

class Post {

    private var postid: String = ""
    private var postimage: String = ""
    private var publisher: String = ""
    private var description: String = ""

    constructor()

    constructor(postid: String, postimage: String, publisher: String, description: String) {
        this.postid = postid
        this.postimage = postimage
        this.publisher = publisher
        this.description = description
    }

    fun getPostid(): String{
        return postid
    }

    fun getPostimage(): String{
        return postimage
    }

    fun getPublisher(): String{
        return publisher
    }

    fun getDescription(): String{
        return description
    }

    fun serPostid(postid: String)
    {
        this.postid = postid
    }

    fun serPostimage(postimage: String)
    {
        this.postimage = postimage
    }


    fun serPublisher(publisher: String)
    {
        this.publisher = publisher
    }

    fun serDescription(description: String)
    {
        this.description = description
    }



}