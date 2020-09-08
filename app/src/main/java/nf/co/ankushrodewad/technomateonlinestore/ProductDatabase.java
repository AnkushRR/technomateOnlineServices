package nf.co.ankushrodewad.technomateonlinestore;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ProductEntity.class, CartEntity.class}, version = 1, exportSchema = false)
public abstract class ProductDatabase extends RoomDatabase {

    public abstract DaoProductEntity daoProductEntity();

    public abstract DaoCartActivity daoCartActivity();


}
