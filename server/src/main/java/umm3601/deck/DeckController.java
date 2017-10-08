package umm3601.deck;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.types.ObjectId;
import spark.Request;
import spark.Response;

import java.util.Iterator;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;



public class DeckController {
    private final Gson gson;
    private MongoDatabase database;
    private final MongoCollection<Document> deckCollection;


    public DeckController(MongoDatabase database){
        gson = new Gson();
        this.database = database;
        deckCollection = database.getCollection("decks");
    }

    public String getDeck(Request req, Response res){
        res.type("application/json");
        String id = req.params("id");
        String deck;
        try {
            deck = getDeck(id);
        }
        catch (IllegalArgumentException e){
            res.status(400);
            res.body("The requested user id " + id + " wasn't a legal Mongo Object ID.\n" +
                "See 'https://docs.mongodb.com/manual/reference/method/ObjectId/' for more info.");
            return "";
        }

        if(deck != null){
            return deck;
        }
        else {
            res.status(404);
            res.body("The requested deck with id " + id + " was not found");
        }
        return null;
    }





    public String getDeck(String id){
        Iterable<Document> jsonDecks
            = deckCollection
            .find(eq("_id", new ObjectId(id)));
        Iterator<Document> iterator = jsonDecks.iterator();
        if (iterator.hasNext()) {
            Document deck = iterator.next();
            return deck.toJson();
        } else {
            // We didn't find the desired deck
            return null;
        }
    }



    public boolean addNewDeck(Request req, Response res)
    {

        res.type("application/json");
        Object o = JSON.parse(req.body());
        try {
            if(o.getClass().equals(BasicDBObject.class))
            {
                try {
                    BasicDBObject dbO = (BasicDBObject) o;

                    String name = dbO.getString("name");


                    Object[] cards = new Object[20];



                    return addNewDeck(name, cards);
                }
                catch(NullPointerException e)
                {
                    System.err.println("A value was malformed or omitted, new deck request failed.");
                    return false;
                }

            }
            else
            {
                System.err.println("Expected BasicDBObject, received " + o.getClass());
                return false;
            }
        }
        catch(RuntimeException ree)
        {
            ree.printStackTrace();
            return false;
        }
    }



    /**
     *
     * @param name
     * @param cards

     * @return
     */
    public boolean addNewDeck(String name, Object[] cards) {

        Document newDeck = new Document();
        newDeck.append("name", name);
        newDeck.append("cards", cards);



        try {
            deckCollection.insertOne(newDeck);
        }
        catch(MongoException me)
        {
            me.printStackTrace();
            return false;
        }

        return true;
    }


}