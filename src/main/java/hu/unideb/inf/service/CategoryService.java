package hu.unideb.inf.service;

import hu.unideb.inf.exceptions.CategoryAlreadyExistsException;
import hu.unideb.inf.exceptions.CategoryNotFoundException;
import hu.unideb.inf.mapper.EntityMapper;
import hu.unideb.inf.model.dto.CategoryDto;
import hu.unideb.inf.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import hu.unideb.inf.model.entity.Category;

import java.util.List;

@Service
@Transactional
public class CategoryService{
    private final CategoryRepository categoryRepository;
    private final EntityMapper mapper;
    private final MessageSource messageSource;

    public CategoryService(
            CategoryRepository categoryRepository,
            EntityMapper mapper,
            MessageSource messageSource) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
        this.messageSource = messageSource;
    }

    public CategoryDto addCategory(CategoryDto dto) {
        if (categoryRepository.existsById(dto.getName())) {
            throw new CategoryAlreadyExistsException(
                    message("category.already-exists", dto.getName()));
        }
        Category saved = categoryRepository.save(mapper.dtoToCategory(dto));
        return mapper.categoryToDto(saved);
    }

    public void deleteByName(String name) {
        Category category = findCategoryOrThrow(name);
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(mapper::categoryToDto)
                .toList();
    }

    public CategoryDto updateCategory(String name, String photo) {
        Category category = findCategoryOrThrow(name);
        category.setPhoto(photo);

        return mapper.categoryToDto(categoryRepository.save(category));
    }

    // helpers
    private String message(String code, Object... arguments){
        return messageSource.getMessage(code, arguments, LocaleContextHolder.getLocale());
    }

    private Category findCategoryOrThrow(String name) {
        return categoryRepository.findById(name)
                .orElseThrow(() ->
                        new CategoryNotFoundException(message("category.not-found", name)));
    }
}
