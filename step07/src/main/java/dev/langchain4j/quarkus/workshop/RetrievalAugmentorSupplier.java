package dev.langchain4j.quarkus.workshop;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.ReRankingContentAggregator;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Supplier;

@ApplicationScoped
public class RetrievalAugmentorSupplier implements Supplier<RetrievalAugmentor> {

    private final QueryTransformer queryTransformer;
    private final ContentRetriever contentRetriever;
    private final ContentAggregator contentAggregator;

    RetrievalAugmentorSupplier(ChatLanguageModel chatLanguageModel,
                               EmbeddingStore<TextSegment> embeddingStore,
                               EmbeddingModel embeddingModel,
                               ScoringModel scoringModel) {
        this.queryTransformer = new CompressingQueryTransformer(chatLanguageModel);

        this.contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.0)
                .build();

        this.contentAggregator = ReRankingContentAggregator.builder()
                .scoringModel(scoringModel)
                .minScore(0.0)
                .build();
    }

    @Override
    public RetrievalAugmentor get() {
        return DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .contentRetriever(contentRetriever)
                .contentAggregator(contentAggregator)
                .build();
    }
}