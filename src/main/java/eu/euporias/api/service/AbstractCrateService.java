package eu.euporias.api.service;

import io.crate.action.sql.SQLRequest;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crate.core.CrateAction;
import org.springframework.data.crate.core.CrateTemplate;
import org.springframework.data.crate.core.mapping.annotations.Table;
import org.springframework.data.crate.repository.CrateRepository;

public class AbstractCrateService<T, R extends CrateRepository<T, ID>, ID extends Serializable> {

	public AbstractCrateService(Class<T> clazz, R repo) {
		super();
		this.repo = repo;
		this.refreshTableAction = new CrateAction() {
			@Override
			public String getSQLStatement() {
				return "refresh table " + tableName(clazz);
			}
			@Override
			public SQLRequest getSQLRequest() {
				return new SQLRequest(getSQLStatement());
			}
		};
	}
	
	private final CrateAction refreshTableAction;

	private String tableName(Class<T> clazz) {
		for (Annotation annotation : clazz.getAnnotations()) {
			if(annotation instanceof Table){
				return ((Table) annotation).name();
			}
		}
		throw new IllegalStateException("Table name not found");
	}	

    public void refresh() {
        crateTemplate.execute(refreshTableAction);
    }
	
	private final R repo;
	
	public R getRepo() {
		return repo;
	}

	protected CrateTemplate getCrateTemplate() {
		return crateTemplate;
	}

	@Autowired private CrateTemplate crateTemplate;
	
}
