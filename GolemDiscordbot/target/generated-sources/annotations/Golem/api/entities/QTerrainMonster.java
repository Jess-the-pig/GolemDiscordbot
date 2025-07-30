package Golem.api.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTerrainMonster is a Querydsl query type for TerrainMonster
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTerrainMonster extends EntityPathBase<TerrainMonster> {

    private static final long serialVersionUID = 1984255516L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTerrainMonster terrainMonster = new QTerrainMonster("terrainMonster");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMonsters monster;

    public final QTerrains terrain;

    public QTerrainMonster(String variable) {
        this(TerrainMonster.class, forVariable(variable), INITS);
    }

    public QTerrainMonster(Path<? extends TerrainMonster> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTerrainMonster(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTerrainMonster(PathMetadata metadata, PathInits inits) {
        this(TerrainMonster.class, metadata, inits);
    }

    public QTerrainMonster(Class<? extends TerrainMonster> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.monster = inits.isInitialized("monster") ? new QMonsters(forProperty("monster")) : null;
        this.terrain = inits.isInitialized("terrain") ? new QTerrains(forProperty("terrain")) : null;
    }

}

