package hu.unideb.inf.service;

import hu.unideb.inf.exceptions.CardNotFoundException;
import hu.unideb.inf.exceptions.InvalidNValueException;
import hu.unideb.inf.mapper.EntityMapper;
import hu.unideb.inf.repository.CardRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import hu.unideb.inf.model.entity.Card;
import hu.unideb.inf.model.dto.CardDto;

import java.util.List;


@Service
@Transactional
public class CardService {
    private final CardRepository cardRepository;
    private final EntityMapper mapper;
    private final MessageSource messageSource;

    public CardService(
            CardRepository cardRepository,
            EntityMapper mapper,
            MessageSource messageSource) {
        this.cardRepository = cardRepository;
        this.mapper = mapper;
        this.messageSource = messageSource;
    }

    public CardDto createCard(CardDto dto) {
        Card card = mapper.dtoToCard(dto);

        Card saved = cardRepository.save(card);
        return mapper.cardToDto(saved);
    }

    @Transactional(readOnly = true)
    public CardDto getById(Integer id){
        return mapper.cardToDto(findCardOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<CardDto> getNCardsWithCategory(Integer n, Integer categoryId) {
        if (n == null || n < 1) {
            throw new InvalidNValueException(
                    message("card.n-value.invalid", n));
        }
        return cardRepository.findByCategoryId(categoryId).stream()
                .limit(n)
                .map(mapper::cardToDto)
                .toList();
    }

    public void deleteById(Integer id) {
        Card card = findCardOrThrow(id);
        cardRepository.delete(card);
    }

    private String message(String code, Object... arguments){
        return messageSource.getMessage(code, arguments, LocaleContextHolder.getLocale());
    }
    // helpers
    private Card findCardOrThrow(Integer id) {
        return cardRepository.findById(id)
                .orElseThrow(() ->
                    new CardNotFoundException(message("card.not-found", id)));
    }
}
