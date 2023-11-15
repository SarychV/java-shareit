package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.ItemAnswer;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.PageParams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto addNewRequest(ItemRequestDto itemRequestDto, Integer requesterId) {
        User requester = userRepository.findById(requesterId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        if (itemRequestDto.getDescription() == null) {
            throw new ValidationException("Описание вещи не может быть пустым.");
        }

        ItemRequest request =
                itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, requesterId));
        return ItemRequestMapper.toItemRequestDto(request);
    }

    @Override
    public List<ItemRequestWithAnswersDto> getItemRequestListWithAnswersByOwner(Integer ownerId) {
        List<ItemRequestWithAnswersDto> requestsWithAnswers = new ArrayList<>();

        User owner = userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        List<ItemRequest> ownerRequests = itemRequestRepository.findByRequesterId(
                ownerId, Sort.by(Sort.Direction.DESC, "created"));
        for(ItemRequest request : ownerRequests) {
            List<ItemAnswer> answers = itemRepository.findAllByRequestId(request.getId());
            requestsWithAnswers.add(new ItemRequestWithAnswersDto(request, answers));
        }
        return requestsWithAnswers;
    }

    @Override
    public List<ItemRequestWithAnswersDto> getAllItemRequests(Integer requesterId, Integer from, Integer size) {
        List<ItemRequestWithAnswersDto> requestsWithAnswers = new ArrayList<>();

        User requester = userRepository.findById(requesterId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));
        PageParams.validate(from, size);

        Sort sortByCreated = Sort.by(Sort.Direction.DESC,"created");
        Pageable page = PageRequest.of(from, size, sortByCreated);
        Page<ItemRequest> pageSelection = itemRequestRepository.findByRequesterIdNot(requesterId, page);

        List<ItemRequest> listSelection = pageSelection.stream().collect(Collectors.toList());
        for (ItemRequest request : listSelection) {
            List<ItemAnswer> answers = itemRepository.findAllByRequestId(request.getId());
            requestsWithAnswers.add(new ItemRequestWithAnswersDto(request, answers));
        }
        return requestsWithAnswers;
    }

    @Override
    public ItemRequestWithAnswersDto getItemRequestById(Integer userId, Long requestId) {
        User owner = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден."));

        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос не найден."));

        List<ItemAnswer> answers = itemRepository.findAllByRequestId(request.getId());
        return new ItemRequestWithAnswersDto(request, answers);
    }
}
