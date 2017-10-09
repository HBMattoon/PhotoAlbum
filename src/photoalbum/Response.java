/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photoalbum;

class Response {
    App photos;
    //Object photos;
}

class App {
    String total;
    int page;
    int pages;
    int perpage;
    PhotoInfo [] photo;

}

class PhotoInfo {
    int isfamily;
    int ispublic;
    int isfriend;
    String id;
    int farm;
    String title;
    int context;
    String owner;
    String secret;
    String server;
    float longitude;
    float latitude;
    int accuracy;
}

    