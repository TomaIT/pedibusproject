package it.polito.ai.pedibusproject.controller.model;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.HashSet;
import java.util.Set;

@Data
public class PageMy<T> {
    Set<T> content;
    long totalElements;
    long totalPages;
    long pageNumber;
    long numberOfElements;
    boolean last;
    boolean first;

    public PageMy(Page<T> page){
        this.content=new HashSet<>(page.getContent());
        this.totalElements=page.getTotalElements();
        this.totalPages=page.getTotalPages();
        this.pageNumber=page.getNumber();
        this.numberOfElements=page.getNumberOfElements();
        this.last=page.isLast();
        this.first=page.isFirst();
    }
}
