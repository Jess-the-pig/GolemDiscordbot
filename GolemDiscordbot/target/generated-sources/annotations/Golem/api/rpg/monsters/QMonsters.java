package Golem.api.rpg.monsters;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMonsters is a Querydsl query type for Monsters
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMonsters extends EntityPathBase<Monsters> {

    private static final long serialVersionUID = 806753651L;

    public static final QMonsters monsters = new QMonsters("monsters");

    public final NumberPath<Integer> ac = createNumber("ac", Integer.class);

    public final StringPath align = createString("align");

    public final NumberPath<Integer> chaScore = createNumber("chaScore", Integer.class);

    public final NumberPath<Integer> conScore = createNumber("conScore", Integer.class);

    public final StringPath cr = createString("cr");

    public final DateTimePath<java.time.LocalDateTime> dateCreated = createDateTime("dateCreated", java.time.LocalDateTime.class);

    public final NumberPath<Integer> dexScore = createNumber("dexScore", Integer.class);

    public final NumberPath<Integer> hp = createNumber("hp", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> intScore = createNumber("intScore", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> lastUpdated = createDateTime("lastUpdated", java.time.LocalDateTime.class);

    public final BooleanPath legendary = createBoolean("legendary");

    public final StringPath name = createString("name");

    public final StringPath playerName = createString("playerName");

    public final StringPath size = createString("size");

    public final StringPath source = createString("source");

    public final StringPath speed = createString("speed");

    public final NumberPath<Integer> strScore = createNumber("strScore", Integer.class);

    public final StringPath type = createString("type");

    public final StringPath url = createString("url");

    public final NumberPath<Long> userid = createNumber("userid", Long.class);

    public final StringPath username = createString("username");

    public final NumberPath<Integer> wisScore = createNumber("wisScore", Integer.class);

    public QMonsters(String variable) {
        super(Monsters.class, forVariable(variable));
    }

    public QMonsters(Path<? extends Monsters> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMonsters(PathMetadata metadata) {
        super(Monsters.class, metadata);
    }

}

