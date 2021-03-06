/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.unbosque.swii;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.pool2.BaseObjectPool;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.sql.PreparedStatement;
import sun.net.smtp.SmtpClient;

/**
 *
 * @author Alejandro
 */
public class PoolTest {

    public static final String pwd = "YckGwYC8gW";

    @Test(expectedExceptions = org.postgresql.util.PSQLException.class,
            expectedExceptionsMessageRegExp = ".*too many connections.*"
    )
    public void soloDebeCrear5Conexiones() throws Exception {
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo3_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        for (int i = 0; i < 6; i++) {
            pool.borrowObject();
        }
    }

    @Test
    public void aprendiendoAControlarLasConexiones() throws Exception {
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo3_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        for (int i = 0; i < 6; i++) {
            Connection c = pool.borrowObject();
            pool.returnObject(c);
        }
    }

    @Test
    public void quePasaCuandoSeCierraUnaConexionAntesDeRetornarla() throws Exception {
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo3_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);
        Connection c = pool.borrowObject();
        pool.close();
        pool.returnObject(c);
    }

    @Test
    public void quePasaCuandoSeRetornaUnaconexionContransaccionIniciada() {
        FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo3_5", pwd);
    }

    @Test(threadPoolSize = 5, invocationCount = 5)
    public void midaTiemposParaInsertar1000RegistrosConSingleton() throws SQLException, ClassNotFoundException {
        for (int i = 1; i <= 1000; i++) {
            SingletonConnection s = (SingletonConnection) SingletonConnection.getConnection();
            String sql = "INSERT INTO grupo3.Hilos(Hilo, numero, numero) VALUES (?, ?, ?);";
        }

    }
    
     FabricaConexiones fc = new FabricaConexiones("aretico.com", 5432, "software_2", "grupo3_5", pwd);
        ObjectPool<Connection> pool = new GenericObjectPool<Connection>(fc);

    @Test(threadPoolSize = 5, invocationCount = 5)
    public void midaTiemposParaInsertar1000RegistrosConObjectPool() throws Exception {
       
        Connection c = pool.borrowObject();
        PreparedStatement pst = null;
        String sql = "INSERT INTO grupo3.hilosgrupo3(hilo_name, numero_llegada, tiempo) VALUES (?, ?, now());";
        pst = c.prepareStatement(sql);
        for (int i = 1; i <= 1000; i++) {
            pst.setString(1, Thread.currentThread().getName());
            pst.setString(2, Thread.currentThread().getName() + "-" + i);
            pst.execute();
        }
        pool.returnObject(c);
    }

}
