package eu.euporias.api.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Bean
	public DataSource primaryDataSource() {
		Properties dsProps = new Properties();
		dsProps.put("url", getDataSourceUrl());
		dsProps.put("user", jdbcUsername);
		dsProps.put("password", jdbcPassword);
		Properties configProps = new Properties();
		configProps.put("connectionTestQuery", "select 1 from dual");
		configProps.put("dataSourceClassName", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
		configProps.put("dataSourceProperties", dsProps);
		HikariConfig hc = new HikariConfig(configProps);
		HikariDataSource ds = new HikariDataSource(hc);
		return ds;
	}

	@Bean
	public DSLContext dsl(DataSource dataSource) {
		DataSourceConnectionProvider connectionProvider = new DataSourceConnectionProvider(dataSource);
		DefaultConfiguration configuration = new DefaultConfiguration();
		configuration.setConnectionProvider(connectionProvider);
		configuration.setSQLDialect(SQLDialect.MYSQL);
		return new DefaultDSLContext(configuration);
	}
	
	private String getDataSourceUrl(){
		return "jdbc:mysql://" + jdbcHost + ":" + jdbcPort +"/" + jdbcDatabase;
	}
	
	private @Value("${jdbc.host}") String jdbcHost;
	private @Value("${jdbc.port}") String jdbcPort;
	private @Value("${jdbc.database}") String jdbcDatabase;
	private @Value("${jdbc.username}") String jdbcUsername;
	private @Value("${jdbc.password}") String jdbcPassword;
		
}