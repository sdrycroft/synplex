package name.rycroft.simon.synplex;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@Singleton
public class SyncViews {

    private static final Logger logger = LoggerFactory.getLogger(SyncViews.class);
    private static final String INSERT_INTO_METADATA_ITEM_VIEWS = """
            INSERT INTO metadata_item_views (
                account_id,
                guid,
                metadata_type,
                library_section_id,
                grandparent_title,
                parent_index,
                parent_title,
                `index`,
                title,
                thumb_url,
                viewed_at,
                grandparent_guid,
                originally_available_at,
                device_id
            )
            SELECT
                ?,
                guid,
                metadata_type,
                library_section_id,
                grandparent_title,
                parent_index,
                parent_title,
                `index`,
                title,
                thumb_url,
                viewed_at,
                grandparent_guid,
                originally_available_at,
                device_id
            FROM metadata_item_views miv
            WHERE account_id = ?
            AND miv.guid NOT IN (
                SELECT guid
                FROM metadata_item_views
                WHERE account_id = ?
            );
            """;
    private static final String INSERT_INTO_METADATA_ITEM_SETTINGS = """
            INSERT INTO metadata_item_settings (
                account_id,
                guid,
                rating,
                view_offset,
                view_count,
                last_viewed_at,
                created_at,
                updated_at,
                skip_count,
                last_skipped_at,
                changed_at,
                extra_data,
                last_rated_at
            )
            SELECT
                ?,
                guid,
                rating,
                view_offset,
                view_count,
                last_viewed_at,
                created_at,
                updated_at,
                skip_count,
                last_skipped_at,
                changed_at,
                extra_data,
                last_rated_at
            FROM metadata_item_settings
            WHERE account_id = ?
            AND guid NOT IN (
                SELECT guid
                FROM metadata_item_settings
                WHERE account_id = ?
            );
            """;
    private static final String UPDATE_METADATA_ITEM_SETTINGS = """
            WITH up AS (
                SELECT lft.* FROM metadata_item_settings lft
                INNER JOIN metadata_item_settings rgt ON lft.guid = rgt.guid AND lft.account_id = ? AND rgt.account_id = ?
                WHERE lft.updated_at > rgt.updated_at
            )
            UPDATE metadata_item_settings SET
                rating = (SELECT rating FROM up WHERE guid = metadata_item_settings.guid),
                view_offset = (SELECT view_offset FROM up WHERE guid = metadata_item_settings.guid),
                last_viewed_at  = (SELECT last_viewed_at FROM up WHERE guid = metadata_item_settings.guid),
                created_at = (SELECT created_at FROM up WHERE guid = metadata_item_settings.guid),
                updated_at = (SELECT updated_at FROM up WHERE guid = metadata_item_settings.guid),
                skip_count = (SELECT skip_count FROM up WHERE guid = metadata_item_settings.guid),
                last_skipped_at = (SELECT last_skipped_at FROM up WHERE guid = metadata_item_settings.guid),
                changed_at = (SELECT changed_at FROM up WHERE guid = metadata_item_settings.guid),
                extra_data = (SELECT extra_data FROM up WHERE guid = metadata_item_settings.guid),
                last_rated_at = (SELECT last_rated_at FROM up WHERE guid = metadata_item_settings.guid),
                view_count = (SELECT view_count FROM up WHERE guid = metadata_item_settings.guid)
            WHERE guid IN (SELECT guid FROM up)
              AND account_id = ?
            """;
    private final Database database;

    @Inject
    public SyncViews(Database database) {
        this.database = database;
    }

    public void sync(long id) {
        try {
            logger.info("Inserting views.");
            database.update(INSERT_INTO_METADATA_ITEM_VIEWS, id, 1, id);
            database.update(INSERT_INTO_METADATA_ITEM_VIEWS, 1, id, 1);
            logger.info("Inserting settings.");
            database.update(INSERT_INTO_METADATA_ITEM_SETTINGS, id, 1, id);
            database.update(INSERT_INTO_METADATA_ITEM_SETTINGS, 1, id, 1);
            logger.info("Updating settings.");
            database.update(UPDATE_METADATA_ITEM_SETTINGS, 1, id, id);
            database.update(UPDATE_METADATA_ITEM_SETTINGS, id, id, 1);
        } catch (SQLException ignored) {
        }
    }
}
