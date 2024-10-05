package repository;

import domain.Goods;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRepository extends CrudRepository<Goods, String> {
}
