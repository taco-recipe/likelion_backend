package org.example.backendproject.board.searchlog.repository;

import org.example.backendproject.board.searchlog.domain.SearchLogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchLogEsRepository extends ElasticsearchRepository<SearchLogDocument,String> {


}
