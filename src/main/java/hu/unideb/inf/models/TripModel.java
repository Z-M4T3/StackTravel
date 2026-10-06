package hu.unideb.inf.models;

public class TripModel {
    private int id, indx, userId,cardId;

    public TripModel(int id, int indx, int userId, int cardId) {
        this.id = id;
        this.indx = indx;
        this.userId = userId;
        this.cardId = cardId;
    }

    public void setIndx(int indx) {
        this.indx = indx;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getId() {
        return id;
    }

    public int getIndx() {
        return indx;
    }

    public int getUserId() {
        return userId;
    }

    public int getCardId() {
        return cardId;
    }
}

