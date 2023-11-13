package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemRequestDto addNewRequest(ItemRequestDto itemRequestDto, Integer requesterId) {
        User requester = userRepository.findById(requesterId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));
        ItemRequest request =
                itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, requester));
        return ItemRequestMapper.toItemRequestDto(request);
    }

    @Override
    public List<ItemRequestDto> getItemRequestListWithAnswersByOwner(Integer ownerId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer requesterId, Integer from, Integer size) {
        return null;
    }

    @Override
    public ItemRequestDto getItemRequestById(Integer userId, Long requestId) {
        return null;
    }
}
