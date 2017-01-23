# How to configure the project

## Set up the project

[Android Studio](https://developer.android.com/studio/index.html) is being used to develop the app. For storing the data a [MongoDB](https://www.mongodb.com) server and database are being used and you should set up your own MongoDB server to try the app and your changes.

### Set up MongoDB

First of all you have to set up a MongoDB server in your computer. You can read in the [MongoDB installation guide](https://docs.mongodb.com/manual/installation) how to do it.

After that you can use your local IP to connect to the MongoDB server. The IP is set in the Variable class. You won't need to change the port as the `27017` is the MongoDB default port. If you do not want to configure the authentification in your MongoDB database (it will be faster), change this line in the Variables class:

``` Java
public static final String mongo_uri = "mongodb://androidUser:password@" + IP + ":" + PORT + "/bipolarDatabase?authMechanism=MONGODB-CR";
```

for this one:

``` Java
public static final String mongo_uri = "mongodb://" + IP + ":" + PORT + "/test";
```

Installing your own MongoDB client allows you to have your local database and by using the MongoDB client check what has been stored. So, it is a good idea to set a MongoDB client too.
