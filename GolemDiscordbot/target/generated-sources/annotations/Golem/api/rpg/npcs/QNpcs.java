package Golem.api.rpg.npcs;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNpcs is a Querydsl query type for Npcs
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNpcs extends EntityPathBase<Npcs> {

    private static final long serialVersionUID = -183616365L;

    public static final QNpcs npcs = new QNpcs("npcs");

    public final StringPath background = createString("background");

    public final NumberPath<Integer> base_hp = createNumber("base_hp", Integer.class);

    public final StringPath char_id = createString("char_id");

    public final StringPath class_other = createString("class_other");

    public final StringPath class_starting = createString("class_starting");

    public final NumberPath<Integer> class_starting_level = createNumber("class_starting_level", Integer.class);

    public final StringPath date_modified = createString("date_modified");

    public final DateTimePath<java.time.LocalDateTime> dateCreated = createDateTime("dateCreated", java.time.LocalDateTime.class);

    public final StringPath feats = createString("feats");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath inventory = createString("inventory");

    public final DateTimePath<java.time.LocalDateTime> lastUpdated = createDateTime("lastUpdated", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> notes_len = createNumber("notes_len", Integer.class);

    public final StringPath race = createString("race");

    public final NumberPath<Integer> stats_1 = createNumber("stats_1", Integer.class);

    public final NumberPath<Integer> stats_2 = createNumber("stats_2", Integer.class);

    public final NumberPath<Integer> stats_3 = createNumber("stats_3", Integer.class);

    public final NumberPath<Integer> stats_4 = createNumber("stats_4", Integer.class);

    public final NumberPath<Integer> stats_5 = createNumber("stats_5", Integer.class);

    public final NumberPath<Integer> stats_6 = createNumber("stats_6", Integer.class);

    public final StringPath subclass_other = createString("subclass_other");

    public final StringPath subclass_starting = createString("subclass_starting");

    public final NumberPath<Integer> total_level = createNumber("total_level", Integer.class);

    public QNpcs(String variable) {
        super(Npcs.class, forVariable(variable));
    }

    public QNpcs(Path<? extends Npcs> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNpcs(PathMetadata metadata) {
        super(Npcs.class, metadata);
    }

}

