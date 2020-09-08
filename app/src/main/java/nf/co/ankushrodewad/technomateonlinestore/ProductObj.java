package nf.co.ankushrodewad.technomateonlinestore;

public class ProductObj {
    String title;
    double price;
    String id;
    String short_description;
    String long_description;
    String imgUrl;

    public ProductObj() {
        //Public empty constructor was needed for fireStore
    }

    public ProductObj(String title, double price, String id, String short_description, String long_description, String imgUrl) {
        this.title = title;
        this.id = id;
        this.price = price;
        this.short_description = short_description;
        this.long_description = long_description;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getLong_description() {
        return long_description;
    }

    public void setLong_description(String long_description) {
        this.long_description = long_description;
    }
}
