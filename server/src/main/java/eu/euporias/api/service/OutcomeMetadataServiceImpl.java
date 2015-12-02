package eu.euporias.api.service;

import static eu.euporias.api.util.DSLNames.field;
import static eu.euporias.api.util.DSLNames.table;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectForUpdateStep;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Product;

@Service("outcomeMetadataService")
public class OutcomeMetadataServiceImpl implements OutcomeMetadataService {

	@Override
	public Integer countByParameterName(Application application, Product product, String parameterName){
		Table<Record> outcomeTable = table("outcome");
		Table<Record> outcomeParamsTable = table("outcome_parameters");
		Field<String> parametersField = field(String.class, outcomeParamsTable, "parameters");
		Field<Integer> countDistinctParametersField = DSL.countDistinct(parametersField);
		SelectForUpdateStep<Record1<Integer>> query = dslContext.select(countDistinctParametersField)
			.from(outcomeTable).join(outcomeParamsTable)
				.on(field(outcomeTable, "id").eq(field(outcomeParamsTable, "outcome_id")))
			.where(
				field(outcomeTable, "application_id").eq(application.getId()),
				field(outcomeTable, "product_id").eq(product.getId()),
				field(outcomeParamsTable, "parameters_key").eq(parameterName)
			);
		Integer result = query.fetchOne().value1();
		return result != null ? result : 0;
	}
	
	@Override
	public List<String> findByParameterName(Application application, Product product, String parameterName, Pageable pageable){
    	Table<Record> outcomeTable = table("outcome");
		Table<Record> outcomeParamsTable = table("outcome_parameters");
		Field<String> parametersField = field(String.class, outcomeParamsTable, "parameters");
		SelectForUpdateStep<Record1<String>> query = dslContext.selectDistinct(parametersField)
			.from(outcomeTable).join(outcomeParamsTable)
				.on(field(outcomeTable, "id").eq(field(outcomeParamsTable, "outcome_id")))
			.where(
				field(outcomeTable, "application_id").eq(application.getId()),
				field(outcomeTable, "product_id").eq(product.getId()),
				field(outcomeParamsTable, "parameters_key").eq(parameterName)
			)
			.orderBy(parametersField.desc())
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset());
		return query.fetch(parametersField);
	}

	@Autowired private DSLContext dslContext;

}
