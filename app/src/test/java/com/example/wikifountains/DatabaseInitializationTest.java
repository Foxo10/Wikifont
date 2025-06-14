package com.example.wikifountains;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.example.wikifountains.data.AppDatabase;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RobolectricTestRunner;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class DatabaseInitializationTest {
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase("fuentes_database");
    }

    @Test
    public void initialDataLoadedWithoutDuplicates() throws Exception {
        AppDatabase db1 = AppDatabase.getInstance(context);
        // Esperar a que se complete la carga inicial
        Thread.sleep(1000);
        int firstCount = db1.fuenteDao().countFuentes();

        AppDatabase db2 = AppDatabase.getInstance(context);
        Thread.sleep(500);
        int secondCount = db2.fuenteDao().countFuentes();

        assertEquals(firstCount, secondCount);
        assertEquals(26, firstCount);
    }
}
