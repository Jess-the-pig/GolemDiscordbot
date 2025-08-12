package Golem.api.rpg.campaign;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCampaign is a Querydsl query type for Campaign
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCampaign extends EntityPathBase<Campaign> {

    private static final long serialVersionUID = -24902829L;

    public static final QCampaign campaign = new QCampaign("campaign");

    public final NumberPath<Long> campaignId = createNumber("campaignId", Long.class);

    public final ListPath<Golem.api.rpg.characters.Characters, Golem.api.rpg.characters.QCharacters> characters = this.<Golem.api.rpg.characters.Characters, Golem.api.rpg.characters.QCharacters>createList("characters", Golem.api.rpg.characters.Characters.class, Golem.api.rpg.characters.QCharacters.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> dateCreated = createDateTime("dateCreated", java.time.LocalDateTime.class);

    public final StringPath dm = createString("dm");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastUpdated = createDateTime("lastUpdated", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final StringPath playerCreator = createString("playerCreator");

    public QCampaign(String variable) {
        super(Campaign.class, forVariable(variable));
    }

    public QCampaign(Path<? extends Campaign> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCampaign(PathMetadata metadata) {
        super(Campaign.class, metadata);
    }

}

