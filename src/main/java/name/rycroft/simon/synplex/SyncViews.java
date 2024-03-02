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
    private static final String UPDATE_METADATA_ITEM_SETTINGS_VIEW_COUNT = """
            UPDATE metadata_item_settings
            SET view_count = (
                SELECT COUNT(*) FROM metadata_item_views
                WHERE metadata_item_settings.guid = metadata_item_views.guid
                AND metadata_item_settings.account_id = metadata_item_views.account_id
            )
            WHERE account_id = ?;
            """;
    private final Database database;

    @Inject
    public SyncViews(Database database) {
        this.database = database;
    }

    public void sync(long id) {
        try {
            logger.info("Syncing views.");
            database.update(INSERT_INTO_METADATA_ITEM_VIEWS, id, 1, id);
            database.update(INSERT_INTO_METADATA_ITEM_VIEWS, 1, id, 1);
            logger.info("Syncing settings.");
            database.update(INSERT_INTO_METADATA_ITEM_SETTINGS, id, 1, id);
            database.update(INSERT_INTO_METADATA_ITEM_SETTINGS, 1, id, 1);
            logger.info("Syncing view counts.");
            database.update(UPDATE_METADATA_ITEM_SETTINGS_VIEW_COUNT, id);
            database.update(UPDATE_METADATA_ITEM_SETTINGS_VIEW_COUNT, 1);
        } catch (SQLException ignored) {
        }
    }
}
