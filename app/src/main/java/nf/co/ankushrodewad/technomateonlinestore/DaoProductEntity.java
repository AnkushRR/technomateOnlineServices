package nf.co.ankushrodewad.technomateonlinestore;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DaoProductEntity {

    @Insert
    void insertOnlySingleProduct(ProductEntity productEntity);

    @Insert
    void insertMultipleProducts(List<ProductEntity> moviesList);

    @Query("SELECT * FROM ProductEntity WHERE id = :product_id")
    ProductEntity fetchOneProductByProductId(String product_id);

    @Update
    void updateProductEntity(ProductEntity productEntity);

    @Delete
    void deleteProductEntity(ProductEntity productEntity);
}
