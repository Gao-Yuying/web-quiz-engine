package engine.models;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class DataPagination<T> {
    protected int totalPages;
    protected int totalElements;
    protected boolean last;
    protected boolean first;
    protected boolean empty;
    protected List<T> content;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public DataPagination<T> getPagination(Page<T> pagedResult) {
        this.setContent(pagedResult.hasContent() ?
                pagedResult.getContent() : new ArrayList<>());
        this.setEmpty(pagedResult.isEmpty());
        this.setFirst(pagedResult.isFirst());
        this.setLast(pagedResult.isLast());
        this.setTotalElements(pagedResult.getNumberOfElements());
        this.setTotalPages(pagedResult.getTotalPages());
        return this;
    }
}
