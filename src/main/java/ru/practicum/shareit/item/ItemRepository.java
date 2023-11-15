package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemAnswer;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner(User owner);

    @Query("select i from Item i " +
    "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
    "or lower(i.description) like lower(concat('%', ?1, '%'))) " +
    "and i.available = TRUE")
    List<Item> searchByNameDescriptionForText(String text);

    List<ItemAnswer> findAllByRequestId(Long requestId);
}
