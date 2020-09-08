package nf.co.ankushrodewad.technomateonlinestore;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DaoCartActivity {

    @Insert
    void insertOnlySingleProduct(CartEntity cartEntity);

    @Insert
    void insertMultipleProducts(List<CartEntity> cartEntities);

    @Query("SELECT * FROM CartEntity WHERE id = :product_id")
    CartEntity fetchOneProductByProductId(String product_id);

    @Update
    void updateProductEntity(CartEntity cartEntity);

    @Delete
    void deleteProductEntity(CartEntity cartEntity);
}