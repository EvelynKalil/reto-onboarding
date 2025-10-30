package co.com.evelyn.onboardingreactivo.r2dbc.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class PostgreSQLConnectionPoolTest {

    @InjectMocks
    private PostgreSQLConnectionPool connectionPool;

    @Mock
    private PostgresqlConnectionProperties properties;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(properties.getHost()).thenReturn("localhost");
        when(properties.getPort()).thenReturn(5432);
        when(properties.getDatabase()).thenReturn("dbName");
        when(properties.getSchema()).thenReturn("schema");
        when(properties.getUsername()).thenReturn("username");
        when(properties.getPassword()).thenReturn("password");
    }

    @Test
    void getConnectionConfigSuccess() {
        assertNotNull(connectionPool.getConnectionConfig(properties));
    }
}
