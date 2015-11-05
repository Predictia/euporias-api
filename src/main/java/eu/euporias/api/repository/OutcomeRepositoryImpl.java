package eu.euporias.api.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectHavingConditionStep;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import eu.euporias.api.model.Application;
import eu.euporias.api.model.Outcome;
import eu.euporias.api.model.ParameterValue;
import eu.euporias.api.model.Product;

public class OutcomeRepositoryImpl implements OutcomeRepositoryCustom {

	@Override
	public Page<Outcome> findOutcomes(Application application, Product product, List<ParameterValue> parameters, Pageable page){
		Table<Record> outcomeTable = table("outcome");
		Table<Record> outcomeParametersTable = table("outcome_parameters");
		Field<Long> outcomeIdField = field(Long.class, outcomeTable, "id");
		SelectHavingConditionStep<Record1<Long>> selectQuery = dsl
			.select(outcomeIdField)
			.from(outcomeTable)
				.join(outcomeParametersTable)
				.on(outcomeIdField.eq(field(Long.class, outcomeParametersTable, "outcome_id")))
			.where(
				field(outcomeParametersTable, "parameters_key").in(
					parameters.stream()
						.map((pv) -> pv.getName())
						.collect(Collectors.toList())
				).and(
					field(outcomeTable, "application_id").eq(application.getId())
				).and(
					field(outcomeTable, "product_id").eq(product.getId())
				)
			)
			.groupBy(outcomeIdField)
			.having(DSL.field("group_concat(parameters order by parameters_key separator '" + CONCAT_SEPARATOR + "')").eq(
				parameters.stream()
					.sorted((pv1, pv2) -> pv1.getName().compareTo(pv2.getName()))
					.map((pv) -> pv.getValue())
					.collect(Collectors.joining(CONCAT_SEPARATOR))
			));
		LOGGER.debug("Query for outcomes: {}", selectQuery.getSQL(ParamType.INLINED));
		return outcomeRepository
			.findByIdInOrderByLastModifiedDateDesc(selectQuery.fetch(outcomeIdField), page);
		
	}
	
	private static Table<Record> table(String name){
		return DSL.table(DSL.name(name));
	}
	
	private static <T> Field<T> field(Class<T> clazz, Table<Record> table, String column){
		return DSL.field(DSL.name(table.getName(), column), clazz);
	}
	
	private static Field<Object> field(Table<Record> table, String column){
		return DSL.field(DSL.name(table.getName(), column));
	}
	
	@Autowired private OutcomeRepository outcomeRepository;
	@Autowired private DSLContext dsl;
	
	private static final String CONCAT_SEPARATOR = "|o|o|";
	private static final Logger LOGGER = LoggerFactory.getLogger(OutcomeRepositoryImpl.class);
	
}
