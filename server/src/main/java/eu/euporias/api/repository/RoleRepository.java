package eu.euporias.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import eu.euporias.api.model.Role;

@RepositoryRestResource(exported=false)
public interface RoleRepository extends PagingAndSortingRepository<Role, Long>{
	
	public Role findByName(@Param("name") String name);
	
}
