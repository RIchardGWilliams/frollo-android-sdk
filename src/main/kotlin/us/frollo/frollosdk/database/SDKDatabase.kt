/*
 * Copyright 2019 Frollo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.frollo.frollosdk.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.sqlcipher.database.SQLiteDatabase.getBytes
import net.sqlcipher.database.SupportFactory
import us.frollo.frollosdk.R
import us.frollo.frollosdk.database.dao.AccountDao
import us.frollo.frollosdk.database.dao.AddressDao
import us.frollo.frollosdk.database.dao.BillDao
import us.frollo.frollosdk.database.dao.BillPaymentDao
import us.frollo.frollosdk.database.dao.BudgetDao
import us.frollo.frollosdk.database.dao.BudgetPeriodDao
import us.frollo.frollosdk.database.dao.CDRConfigurationDao
import us.frollo.frollosdk.database.dao.CardDao
import us.frollo.frollosdk.database.dao.ConsentDao
import us.frollo.frollosdk.database.dao.ContactDao
import us.frollo.frollosdk.database.dao.GoalDao
import us.frollo.frollosdk.database.dao.GoalPeriodDao
import us.frollo.frollosdk.database.dao.ImageDao
import us.frollo.frollosdk.database.dao.MerchantDao
import us.frollo.frollosdk.database.dao.MessageDao
import us.frollo.frollosdk.database.dao.PaydayDao
import us.frollo.frollosdk.database.dao.ProviderAccountDao
import us.frollo.frollosdk.database.dao.ProviderDao
import us.frollo.frollosdk.database.dao.ReportAccountBalanceDao
import us.frollo.frollosdk.database.dao.ServiceOutageDao
import us.frollo.frollosdk.database.dao.TransactionCategoryDao
import us.frollo.frollosdk.database.dao.TransactionDao
import us.frollo.frollosdk.database.dao.TransactionUserTagsDao
import us.frollo.frollosdk.database.dao.UserDao
import us.frollo.frollosdk.model.api.messages.MessageResponse
import us.frollo.frollosdk.model.coredata.address.Address
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Account
import us.frollo.frollosdk.model.coredata.aggregation.merchants.Merchant
import us.frollo.frollosdk.model.coredata.aggregation.provideraccounts.ProviderAccount
import us.frollo.frollosdk.model.coredata.aggregation.providers.Provider
import us.frollo.frollosdk.model.coredata.aggregation.tags.TransactionTag
import us.frollo.frollosdk.model.coredata.aggregation.transactioncategories.TransactionCategory
import us.frollo.frollosdk.model.coredata.aggregation.transactions.Transaction
import us.frollo.frollosdk.model.coredata.bills.Bill
import us.frollo.frollosdk.model.coredata.bills.BillPayment
import us.frollo.frollosdk.model.coredata.budgets.Budget
import us.frollo.frollosdk.model.coredata.budgets.BudgetPeriod
import us.frollo.frollosdk.model.coredata.cards.Card
import us.frollo.frollosdk.model.coredata.cdr.CDRConfiguration
import us.frollo.frollosdk.model.coredata.cdr.Consent
import us.frollo.frollosdk.model.coredata.contacts.Contact
import us.frollo.frollosdk.model.coredata.goals.Goal
import us.frollo.frollosdk.model.coredata.goals.GoalPeriod
import us.frollo.frollosdk.model.coredata.images.Image
import us.frollo.frollosdk.model.coredata.payday.Payday
import us.frollo.frollosdk.model.coredata.reports.ReportAccountBalance
import us.frollo.frollosdk.model.coredata.servicestatus.ServiceOutage
import us.frollo.frollosdk.model.coredata.user.User

@Database(
    entities = [
        User::class,
        MessageResponse::class,
        Provider::class,
        ProviderAccount::class,
        Account::class,
        Transaction::class,
        TransactionCategory::class,
        Merchant::class,
        ReportAccountBalance::class,
        Bill::class,
        BillPayment::class,
        TransactionTag::class,
        Goal::class,
        GoalPeriod::class,
        Budget::class,
        BudgetPeriod::class,
        Image::class,
        Consent::class,
        CDRConfiguration::class,
        Contact::class,
        Card::class,
        Payday::class,
        Address::class,
        ServiceOutage::class
    ],
    version = 16, exportSchema = true
)

@TypeConverters(Converters::class)
abstract class SDKDatabase : RoomDatabase() {

    internal abstract fun users(): UserDao
    internal abstract fun messages(): MessageDao
    internal abstract fun providers(): ProviderDao
    internal abstract fun providerAccounts(): ProviderAccountDao
    internal abstract fun accounts(): AccountDao
    internal abstract fun transactions(): TransactionDao
    internal abstract fun transactionCategories(): TransactionCategoryDao
    internal abstract fun merchants(): MerchantDao
    internal abstract fun reportsAccountBalance(): ReportAccountBalanceDao
    internal abstract fun bills(): BillDao
    internal abstract fun billPayments(): BillPaymentDao
    internal abstract fun userTags(): TransactionUserTagsDao
    internal abstract fun goals(): GoalDao
    internal abstract fun goalPeriods(): GoalPeriodDao
    internal abstract fun budgets(): BudgetDao
    internal abstract fun budgetPeriods(): BudgetPeriodDao
    internal abstract fun images(): ImageDao
    internal abstract fun consents(): ConsentDao
    internal abstract fun cdrConfiguration(): CDRConfigurationDao
    internal abstract fun contacts(): ContactDao
    internal abstract fun cards(): CardDao
    internal abstract fun payday(): PaydayDao
    internal abstract fun addresses(): AddressDao
    internal abstract fun serviceOutages(): ServiceOutageDao

    companion object {
        private const val DEFAULT_DATABASE_NAME = "frollosdk-db" // WARNING: DO NOT USE this directly anywhere as the actual DB name is derived by appending the DB name prefix.

        @Volatile
        private var instance: SDKDatabase? = null // Singleton instantiation

        internal fun getInstance(context: Context, dbNamePrefix: String? = null): SDKDatabase {
            return instance ?: synchronized(this) {
                instance ?: create(context, dbNamePrefix).also { instance = it }
            }
        }

        private fun create(context: Context, dbNamePrefix: String? = null): SDKDatabase {
            val state = SQLCipherUtils.getDatabaseState(context, DEFAULT_DATABASE_NAME)
            if (state == SQLCipherUtils.State.UNENCRYPTED) {
                SQLCipherUtils.encrypt(context, DEFAULT_DATABASE_NAME, context.getString(R.string.FrolloSDK_DB_PASSPHRASE).toCharArray())
            }

            val passphrase: ByteArray = getBytes(context.getString(R.string.FrolloSDK_DB_PASSPHRASE).toCharArray())
            val factory = SupportFactory(passphrase)
            val dbName = dbNamePrefix?.let { "$it-$DEFAULT_DATABASE_NAME" } ?: DEFAULT_DATABASE_NAME
            return Room.databaseBuilder(context, SDKDatabase::class.java, dbName)
                .allowMainThreadQueries() // Needed for some tests
                .openHelperFactory(factory)
                // .fallbackToDestructiveMigration()
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7,
                    MIGRATION_6_8,
                    MIGRATION_7_8,
                    MIGRATION_8_9,
                    MIGRATION_9_10,
                    MIGRATION_10_11,
                    MIGRATION_11_12,
                    MIGRATION_12_13,
                    MIGRATION_13_14,
                    MIGRATION_14_15,
                    MIGRATION_15_16
                )
                .build()
        }

        // Copy-paste of auto-generated SQLs from room schema json file
        // located in sandbox code after building under frollo-android-sdk/schemas/$version.json

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) Add new table/entity report_transaction_current and create indexes
                // 2) Add new table/entity report_transaction_history and create indexes
                // 3) Add new table/entity report_group_transaction_history and create indexes
                // 4) Add new table/entity report_account_balance and create indexes

                database.execSQL("CREATE TABLE IF NOT EXISTS `report_transaction_current` (`report_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `day` INTEGER NOT NULL, `linked_id` INTEGER, `linked_name` TEXT, `spend_value` TEXT, `previous_period_value` TEXT, `average_value` TEXT, `budget_value` TEXT, `filtered_budget_category` TEXT, `report_grouping` TEXT NOT NULL)")
                database.execSQL("CREATE INDEX `index_report_transaction_current_report_id` ON `report_transaction_current` (`report_id`)")
                database.execSQL("CREATE UNIQUE INDEX `index_report_transaction_current_linked_id_day_filtered_budget_category_report_grouping` ON `report_transaction_current` (`linked_id`, `day`, `filtered_budget_category`, `report_grouping`)")

                database.execSQL("CREATE TABLE IF NOT EXISTS `report_transaction_history` (`report_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `value` TEXT NOT NULL, `budget` TEXT, `period` TEXT NOT NULL, `filtered_budget_category` TEXT, `report_grouping` TEXT NOT NULL)")
                database.execSQL("CREATE INDEX `index_report_transaction_history_report_id` ON `report_transaction_history` (`report_id`)")
                database.execSQL("CREATE UNIQUE INDEX `index_report_transaction_history_date_period_filtered_budget_category_report_grouping` ON `report_transaction_history` (`date`, `period`, `filtered_budget_category`, `report_grouping`)")

                database.execSQL("CREATE TABLE IF NOT EXISTS `report_group_transaction_history` (`report_group_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `linked_id` INTEGER NOT NULL, `linked_name` TEXT NOT NULL, `value` TEXT NOT NULL, `budget` TEXT, `date` TEXT NOT NULL, `period` TEXT NOT NULL, `filtered_budget_category` TEXT, `report_grouping` TEXT NOT NULL, `transaction_ids` TEXT, `report_id` INTEGER NOT NULL)")
                database.execSQL("CREATE INDEX `index_report_group_transaction_history_report_group_id` ON `report_group_transaction_history` (`report_group_id`)")
                database.execSQL("CREATE UNIQUE INDEX `index_report_group_transaction_history_linked_id_date_period_filtered_budget_category_report_grouping` ON `report_group_transaction_history` (`linked_id`, `date`, `period`, `filtered_budget_category`, `report_grouping`)")

                database.execSQL("CREATE TABLE IF NOT EXISTS `report_account_balance` (`report_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `account_id` INTEGER NOT NULL, `currency` TEXT NOT NULL, `value` TEXT NOT NULL, `period` TEXT NOT NULL)")
                database.execSQL("CREATE INDEX `index_report_account_balance_report_id` ON `report_account_balance` (`report_id`)")
                database.execSQL("CREATE UNIQUE INDEX `index_report_account_balance_account_id_date_period` ON `report_account_balance` (`account_id`, `date`, `period`)")
            }
        }

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) Alter table/entity transaction_model - add columns: "merchant_name", "merchant_phone", "merchant_website", "merchant_location"
                // 2) Add new table/entity bill and create indexes
                // 3) Add new table/entity bill_payment and create indexes
                // 4) Alter table/entity message - add column: "auto_dismiss"

                database.execSQL("ALTER TABLE `transaction_model` ADD COLUMN `merchant_name` TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE `transaction_model` ADD COLUMN `merchant_phone` TEXT")
                database.execSQL("ALTER TABLE `transaction_model` ADD COLUMN `merchant_website` TEXT")
                database.execSQL("ALTER TABLE `transaction_model` ADD COLUMN `merchant_location` TEXT")

                database.execSQL("CREATE TABLE IF NOT EXISTS `bill` (`bill_id` INTEGER NOT NULL, `name` TEXT, `description` TEXT, `bill_type` TEXT NOT NULL, `status` TEXT NOT NULL, `last_amount` TEXT, `due_amount` TEXT NOT NULL, `average_amount` TEXT NOT NULL, `frequency` TEXT NOT NULL, `payment_status` TEXT NOT NULL, `last_payment_date` TEXT, `next_payment_date` TEXT NOT NULL, `category_id` INTEGER, `merchant_id` INTEGER, `account_id` INTEGER, `note` TEXT, PRIMARY KEY(`bill_id`))")
                database.execSQL("CREATE INDEX `index_bill_bill_id` ON `bill` (`bill_id`)")
                database.execSQL("CREATE INDEX `index_bill_merchant_id` ON `bill` (`merchant_id`)")
                database.execSQL("CREATE INDEX `index_bill_category_id` ON `bill` (`category_id`)")
                database.execSQL("CREATE INDEX `index_bill_account_id` ON `bill` (`account_id`)")

                database.execSQL("CREATE TABLE IF NOT EXISTS `bill_payment` (`bill_payment_id` INTEGER NOT NULL, `bill_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `merchant_id` INTEGER, `date` TEXT NOT NULL, `payment_status` TEXT NOT NULL, `frequency` TEXT NOT NULL, `amount` TEXT NOT NULL, `unpayable` INTEGER NOT NULL, PRIMARY KEY(`bill_payment_id`))")
                database.execSQL("CREATE INDEX `index_bill_payment_bill_payment_id` ON `bill_payment` (`bill_payment_id`)")
                database.execSQL("CREATE INDEX `index_bill_payment_bill_id` ON `bill_payment` (`bill_id`)")
                database.execSQL("CREATE INDEX `index_bill_payment_merchant_id` ON `bill_payment` (`merchant_id`)")

                database.execSQL("ALTER TABLE `message` ADD COLUMN `auto_dismiss` INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) Create table transaction_user_tags
                // 2) Alter transaction_model table - add column user_tags

                database.execSQL("CREATE TABLE IF NOT EXISTS transaction_user_tags (name TEXT NOT NULL, count INTEGER DEFAULT 0, last_used_at TEXT, created_at TEXT, PRIMARY KEY(name))")
                database.execSQL("CREATE  INDEX index_transaction_user_tags_name ON transaction_user_tags (name)")
                database.execSQL("ALTER TABLE transaction_model ADD COLUMN `user_tags` TEXT")
            }
        }

        private val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) Add new table/entity goal and create indexes
                // 2) Add new table/entity goal_period and create indexes
                // 3) Alter table/entity account - add column: "goal_ids"

                database.execSQL("CREATE TABLE IF NOT EXISTS `goal` (`goal_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `image_url` TEXT, `account_id` INTEGER, `type` TEXT, `sub_type` TEXT, `tracking_status` TEXT NOT NULL, `tracking_type` TEXT NOT NULL, `status` TEXT NOT NULL, `frequency` TEXT NOT NULL, `target` TEXT NOT NULL, `currency` TEXT NOT NULL, `current_amount` TEXT NOT NULL, `period_amount` TEXT NOT NULL, `start_amount` TEXT NOT NULL, `target_amount` TEXT NOT NULL, `start_date` TEXT NOT NULL, `end_date` TEXT NOT NULL, `estimated_end_date` TEXT, `estimated_target_amount` TEXT, `periods_count` INTEGER NOT NULL, `c_period_goal_period_id` INTEGER NOT NULL, `c_period_goal_id` INTEGER NOT NULL, `c_period_start_date` TEXT NOT NULL, `c_period_end_date` TEXT NOT NULL, `c_period_tracking_status` TEXT NOT NULL, `c_period_current_amount` TEXT NOT NULL, `c_period_target_amount` TEXT NOT NULL, `c_period_required_amount` TEXT NOT NULL, PRIMARY KEY(`goal_id`))")
                database.execSQL("CREATE  INDEX `index_goal_goal_id` ON `goal` (`goal_id`)")
                database.execSQL("CREATE  INDEX `index_goal_account_id` ON `goal` (`account_id`)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `goal_period` (`goal_period_id` INTEGER NOT NULL, `goal_id` INTEGER NOT NULL, `start_date` TEXT NOT NULL, `end_date` TEXT NOT NULL, `tracking_status` TEXT, `current_amount` TEXT NOT NULL, `target_amount` TEXT NOT NULL, `required_amount` TEXT NOT NULL, PRIMARY KEY(`goal_period_id`))")
                database.execSQL("CREATE  INDEX `index_goal_period_goal_period_id` ON `goal_period` (`goal_period_id`)")
                database.execSQL("CREATE  INDEX `index_goal_period_goal_id` ON `goal_period` (`goal_id`)")
                database.execSQL("ALTER TABLE `account` ADD COLUMN `goal_ids` TEXT")
            }
        }

        private val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) Alter provider_account table - add column external_id
                // 2) Alter account table - add column external_id
                // 3) Alter transaction_model table - add column external_id
                // 4) Alter table/entity merchant - make smallLogoUrl nullable

                database.execSQL("ALTER TABLE `provider_account` ADD COLUMN `external_id` TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE `account` ADD COLUMN `external_id` TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE `transaction_model` ADD COLUMN `external_id` TEXT NOT NULL DEFAULT ''")

                // START - Alter column smallLogoUrl
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("DROP INDEX IF EXISTS index_merchant_merchant_id")
                database.execSQL("ALTER TABLE merchant RENAME TO orig_merchant")
                database.execSQL("CREATE TABLE IF NOT EXISTS `merchant` (`merchant_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `merchant_type` TEXT NOT NULL, `small_logo_url` TEXT, PRIMARY KEY(`merchant_id`))")
                database.execSQL("CREATE INDEX `index_merchant_merchant_id` ON `merchant` (`merchant_id`)")
                database.execSQL("INSERT INTO merchant(merchant_id, name, merchant_type, small_logo_url) SELECT merchant_id, name, merchant_type, small_logo_url FROM orig_merchant")
                database.execSQL("DROP TABLE orig_merchant")
                database.execSQL("COMMIT")
                // END - Alter column smallLogoUrl
            }
        }

        private val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) Alter report_transaction_history table - add column transaction_tags
                // 2) Alter report_group_transaction_history table - add column transaction_tags
                // 3) Alter goals table - make current period columns nullable

                commonMigrationsReports6To8(database)

                // START - Alter column current period columns to make them nullable
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("DROP INDEX IF EXISTS `index_goal_goal_id`")
                database.execSQL("DROP INDEX IF EXISTS  `index_goal_account_id`")
                database.execSQL("ALTER TABLE goal RENAME TO original_goal")
                database.execSQL("CREATE TABLE IF NOT EXISTS `goal` (`goal_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `image_url` TEXT, `account_id` INTEGER, `type` TEXT, `sub_type` TEXT, `tracking_status` TEXT NOT NULL, `tracking_type` TEXT NOT NULL, `status` TEXT NOT NULL, `frequency` TEXT NOT NULL, `target` TEXT NOT NULL, `currency` TEXT NOT NULL, `current_amount` TEXT NOT NULL, `period_amount` TEXT NOT NULL, `start_amount` TEXT NOT NULL, `target_amount` TEXT NOT NULL, `start_date` TEXT NOT NULL, `end_date` TEXT NOT NULL, `estimated_end_date` TEXT, `estimated_target_amount` TEXT, `periods_count` INTEGER NOT NULL, `c_period_goal_period_id` INTEGER, `c_period_goal_id` INTEGER, `c_period_start_date` TEXT, `c_period_end_date` TEXT, `c_period_tracking_status` TEXT, `c_period_current_amount` TEXT, `c_period_target_amount` TEXT, `c_period_required_amount` TEXT, PRIMARY KEY(`goal_id`))")
                database.execSQL("INSERT INTO goal(goal_id, name, description, image_url, account_id,type,sub_type,tracking_status, tracking_type, status, frequency, target, currency, current_amount, period_amount, start_amount, target_amount, start_date, end_date, estimated_end_date, estimated_target_amount, periods_count, c_period_goal_period_id, c_period_goal_id, c_period_start_date , c_period_end_date, c_period_tracking_status, c_period_current_amount, c_period_target_amount, c_period_required_amount) SELECT goal_id, name, description, image_url,account_id,type,sub_type,tracking_status, tracking_type, status, frequency, target, currency, current_amount, period_amount, start_amount, target_amount, start_date, end_date, estimated_end_date, estimated_target_amount, periods_count, c_period_goal_period_id, c_period_goal_id, c_period_start_date , c_period_end_date, c_period_tracking_status, c_period_current_amount, c_period_target_amount, c_period_required_amount FROM original_goal")
                database.execSQL("DROP TABLE original_goal")
                database.execSQL("CREATE  INDEX `index_goal_goal_id` ON `goal` (`goal_id`)")
                database.execSQL("CREATE  INDEX `index_goal_account_id` ON `goal` (`account_id`)")
                database.execSQL("COMMIT")
                // END - Alter column current period columns to make them nullable
            }
        }

        private fun commonMigrationsReports6To8(database: SupportSQLiteDatabase) {
            database.execSQL("DROP INDEX IF EXISTS `index_report_transaction_history_date_period_filtered_budget_category_report_grouping`")
            database.execSQL("ALTER TABLE `report_transaction_history` ADD COLUMN `transaction_tags` TEXT")
            database.execSQL("CREATE UNIQUE INDEX `index_report_transaction_history_date_period_filtered_budget_category_report_grouping_transaction_tags` ON `report_transaction_history` (`date`, `period`, `filtered_budget_category`, `report_grouping`, `transaction_tags`)")

            database.execSQL("DROP INDEX IF EXISTS `index_report_group_transaction_history_linked_id_date_period_filtered_budget_category_report_grouping`")
            database.execSQL("ALTER TABLE `report_group_transaction_history` ADD COLUMN `transaction_tags` TEXT")
            database.execSQL("CREATE UNIQUE INDEX `index_report_group_transaction_history_linked_id_date_period_filtered_budget_category_report_grouping_transaction_tags` ON `report_group_transaction_history` (`linked_id`, `date`, `period`, `filtered_budget_category`, `report_grouping`, `transaction_tags`)")
        }

        private val MIGRATION_6_8: Migration = object : Migration(6, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) Alter report_transaction_history table - add column transaction_tags
                // 2) Alter report_group_transaction_history table - add column transaction_tags
                // 3) Alter goals table - make current period columns nullable, delete columns type and sub_type, add column metadata, add column c_period_period_index
                // 4) Alter goal_period table - add column period_index
                // 5) Alter messages table - delete column action_open_external, add column metadata, add column action_open_mode

                commonMigrationsReports6To8(database)
                commonMigrationsGoals7To8(database)
                commonMigrationsMessages7To8(database)
            }
        }

        private val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) Alter goals table - delete columns type and sub_type, add column metadata, add column c_period_period_index
                // 2) Alter goal_period table - add column period_index
                // 3) Alter messages table - delete column action_open_external, add column metadata, add column action_open_mode
                commonMigrationsGoals7To8(database)
                commonMigrationsMessages7To8(database)
            }
        }

        private fun commonMigrationsGoals7To8(database: SupportSQLiteDatabase) {
            // START - Alter columns of goal & goal_period tables
            database.execSQL("BEGIN TRANSACTION")

            database.execSQL("DROP INDEX IF EXISTS `index_goal_goal_id`")
            database.execSQL("DROP INDEX IF EXISTS  `index_goal_account_id`")
            database.execSQL("ALTER TABLE goal RENAME TO original_goal")
            database.execSQL("CREATE TABLE IF NOT EXISTS `goal` (`goal_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `image_url` TEXT, `account_id` INTEGER, `tracking_status` TEXT NOT NULL, `tracking_type` TEXT NOT NULL, `status` TEXT NOT NULL, `frequency` TEXT NOT NULL, `target` TEXT NOT NULL, `currency` TEXT NOT NULL, `current_amount` TEXT NOT NULL, `period_amount` TEXT NOT NULL, `start_amount` TEXT NOT NULL, `target_amount` TEXT NOT NULL, `start_date` TEXT NOT NULL, `end_date` TEXT NOT NULL, `estimated_end_date` TEXT, `estimated_target_amount` TEXT, `periods_count` INTEGER NOT NULL, `metadata` TEXT, `c_period_goal_period_id` INTEGER, `c_period_goal_id` INTEGER, `c_period_start_date` TEXT, `c_period_end_date` TEXT, `c_period_tracking_status` TEXT, `c_period_current_amount` TEXT, `c_period_target_amount` TEXT, `c_period_required_amount` TEXT, `c_period_period_index` INTEGER, PRIMARY KEY(`goal_id`))")
            database.execSQL("INSERT INTO goal(goal_id, name, description, image_url, account_id, tracking_status, tracking_type, status, frequency, target, currency, current_amount, period_amount, start_amount, target_amount, start_date, end_date, estimated_end_date, estimated_target_amount, periods_count, c_period_goal_period_id, c_period_goal_id, c_period_start_date , c_period_end_date, c_period_tracking_status, c_period_current_amount, c_period_target_amount, c_period_required_amount) SELECT goal_id, name, description, image_url, account_id, tracking_status, tracking_type, status, frequency, target, currency, current_amount, period_amount, start_amount, target_amount, start_date, end_date, estimated_end_date, estimated_target_amount, periods_count, c_period_goal_period_id, c_period_goal_id, c_period_start_date , c_period_end_date, c_period_tracking_status, c_period_current_amount, c_period_target_amount, c_period_required_amount FROM original_goal")
            database.execSQL("DROP TABLE original_goal")
            database.execSQL("CREATE  INDEX `index_goal_goal_id` ON `goal` (`goal_id`)")
            database.execSQL("CREATE  INDEX `index_goal_account_id` ON `goal` (`account_id`)")

            database.execSQL("DROP INDEX IF EXISTS `index_goal_period_goal_period_id`")
            database.execSQL("DROP INDEX IF EXISTS  `index_goal_period_goal_id`")
            database.execSQL("ALTER TABLE goal_period RENAME TO original_goal_period")
            database.execSQL("CREATE TABLE IF NOT EXISTS `goal_period` (`goal_period_id` INTEGER NOT NULL, `goal_id` INTEGER NOT NULL, `start_date` TEXT NOT NULL, `end_date` TEXT NOT NULL, `tracking_status` TEXT, `current_amount` TEXT NOT NULL, `target_amount` TEXT NOT NULL, `required_amount` TEXT NOT NULL, `period_index` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`goal_period_id`))")
            database.execSQL("INSERT INTO goal_period(goal_period_id, goal_id, start_date, end_date, tracking_status, current_amount, target_amount, required_amount) SELECT goal_period_id, goal_id, start_date, end_date, tracking_status, current_amount, target_amount, required_amount FROM original_goal_period")
            database.execSQL("DROP TABLE original_goal_period")
            database.execSQL("CREATE  INDEX `index_goal_period_goal_period_id` ON `goal_period` (`goal_period_id`)")
            database.execSQL("CREATE  INDEX `index_goal_period_goal_id` ON `goal_period` (`goal_id`)")

            database.execSQL("COMMIT")
            // END - Alter columns of goal & goal_period tables
        }

        private fun commonMigrationsMessages7To8(database: SupportSQLiteDatabase) {
            // START - Alter columns of messages table
            database.execSQL("BEGIN TRANSACTION")
            database.execSQL("DROP INDEX IF EXISTS  `index_message_msg_id`")
            database.execSQL("DROP TABLE message")
            database.execSQL("CREATE TABLE IF NOT EXISTS `message` (`msg_id` INTEGER NOT NULL, `event` TEXT NOT NULL, `user_event_id` INTEGER, `placement` INTEGER NOT NULL, `persists` INTEGER NOT NULL, `read` INTEGER NOT NULL, `interacted` INTEGER NOT NULL, `message_types` TEXT NOT NULL, `title` TEXT, `content_type` TEXT NOT NULL, `auto_dismiss` INTEGER NOT NULL, `metadata` TEXT, `content_main` TEXT, `content_header` TEXT, `content_footer` TEXT, `content_text` TEXT, `content_image_url` TEXT, `content_design_type` TEXT, `content_url` TEXT, `content_width` REAL, `content_height` REAL, `content_autoplay` INTEGER, `content_autoplay_cellular` INTEGER, `content_icon_url` TEXT, `content_muted` INTEGER, `action_title` TEXT, `action_link` TEXT, `action_open_mode` TEXT, PRIMARY KEY(`msg_id`))")
            database.execSQL("CREATE  INDEX `index_message_msg_id` ON `message` (`msg_id`)")
            database.execSQL("COMMIT")
            // END - Alter columns of messages table
        }

        private val MIGRATION_8_9: Migration = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) Delete table report_transaction_current
                // 2) Delete table report_transaction_history
                // 3) Delete table report_group_transaction_history
                // 4) Create table budget
                // 5) Create table budget_period
                // 6) Alter provider table - add column aggregator_type, permissions

                database.execSQL("DROP INDEX IF EXISTS `index_report_transaction_current_report_id`")
                database.execSQL("DROP INDEX IF EXISTS `index_report_transaction_current_linked_id_day_filtered_budget_category_report_grouping`")
                database.execSQL("DROP TABLE report_transaction_current")
                database.execSQL("DROP INDEX IF EXISTS `index_report_transaction_history_report_id`")
                database.execSQL("DROP INDEX IF EXISTS `index_report_transaction_history_date_period_filtered_budget_category_report_grouping_transaction_tags`")
                database.execSQL("DROP TABLE report_transaction_history")
                database.execSQL("DROP INDEX IF EXISTS `index_report_group_transaction_history_report_group_id`")
                database.execSQL("DROP INDEX IF EXISTS `index_report_group_transaction_history_linked_id_date_period_filtered_budget_category_report_grouping_transaction_tags`")
                database.execSQL("DROP TABLE report_group_transaction_history")

                database.execSQL("CREATE TABLE IF NOT EXISTS `budget` (`budget_id` INTEGER NOT NULL, `is_current` INTEGER NOT NULL, `image_url` TEXT, `tracking_status` TEXT NOT NULL, `status` TEXT NOT NULL, `frequency` TEXT NOT NULL, `user_id` INTEGER NOT NULL, `currency` TEXT NOT NULL, `current_amount` TEXT NOT NULL, `period_amount` TEXT NOT NULL, `start_date` TEXT, `type` TEXT NOT NULL, `type_value` TEXT NOT NULL, `periods_count` INTEGER NOT NULL, `metadata` TEXT, `c_period_budget_period_id` INTEGER, `c_period_budget_id` INTEGER, `c_period_start_date` TEXT, `c_period_end_date` TEXT, `c_period_current_amount` TEXT, `c_period_target_amount` TEXT, `c_period_required_amount` TEXT, `c_period_tracking_status` TEXT, `c_period_index` INTEGER, PRIMARY KEY(`budget_id`))")
                database.execSQL("CREATE  INDEX `index_budget_budget_id` ON `budget` (`budget_id`)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `budget_period` (`budget_period_id` INTEGER NOT NULL, `budget_id` INTEGER NOT NULL, `start_date` TEXT NOT NULL, `end_date` TEXT NOT NULL, `current_amount` TEXT NOT NULL, `target_amount` TEXT NOT NULL, `required_amount` TEXT NOT NULL, `tracking_status` TEXT NOT NULL, `index` INTEGER NOT NULL, PRIMARY KEY(`budget_period_id`))")
                database.execSQL("CREATE  INDEX `index_budget_period_budget_period_id` ON `budget_period` (`budget_period_id`)")
                database.execSQL("CREATE  INDEX `index_budget_period_budget_id` ON `budget_period` (`budget_id`)")

                database.execSQL("ALTER TABLE `provider` ADD COLUMN `aggregator_type` TEXT NOT NULL DEFAULT 'YODLEE'")
                database.execSQL("ALTER TABLE `provider` ADD COLUMN `permissions` TEXT")
            }
        }

        private val MIGRATION_9_10: Migration = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) Update tables with new tracking_status values - budget, budget_period, goal, goal_period
                // 2) Alter account table - add column features, product_available, cdr_p_product_id, cdr_p_product_name, cdr_p_product_details_page_url, cdr_p_key_information
                // 3) Alter provider table - add column product_available
                // 4) Alter transaction_model table - add column goal_id
                // 5) New table image
                // 6) New table consent
                // 7) New table cdr_configuration
                // 8) Alter table user - Drop columns register_complete, c_address_*, p_address_* and add columns register_steps, address, mailing_address, foreign_tax, tax_residency, tfn, tin

                database.execSQL(
                    "UPDATE budget  SET tracking_status = (CASE " +
                        " WHEN tracking_status = 'BEHIND' THEN 'BELOW'" +
                        " WHEN tracking_status = 'ON_TRACK' THEN 'EQUAL'" +
                        " WHEN tracking_status = 'AHEAD' THEN 'ABOVE'" +
                        " ELSE 'EQUAL'" +
                        " END) ," +
                        " c_period_tracking_status = (CASE  " +
                        " WHEN c_period_tracking_status = 'BEHIND' THEN 'BELOW'" +
                        " WHEN c_period_tracking_status = 'ON_TRACK' THEN 'EQUAL'" +
                        " WHEN c_period_tracking_status = 'AHEAD' THEN 'ABOVE'" +
                        " ELSE 'EQUAL'" +
                        " END)"
                )

                database.execSQL(
                    "UPDATE goal  SET tracking_status = (CASE " +
                        " WHEN tracking_status = 'BEHIND' THEN 'BELOW'" +
                        " WHEN tracking_status = 'ON_TRACK' THEN 'EQUAL'" +
                        " WHEN tracking_status = 'AHEAD' THEN 'ABOVE'" +
                        " ELSE 'EQUAL'" +
                        " END) ," +
                        " c_period_tracking_status = (CASE  " +
                        " WHEN c_period_tracking_status = 'BEHIND' THEN 'BELOW'" +
                        " WHEN c_period_tracking_status = 'ON_TRACK' THEN 'EQUAL'" +
                        " WHEN c_period_tracking_status = 'AHEAD' THEN 'ABOVE'" +
                        " ELSE 'EQUAL'" +
                        " END)"
                )

                database.execSQL(
                    "UPDATE budget_period  SET tracking_status = (CASE " +
                        " WHEN tracking_status = 'BEHIND' THEN 'BELOW'" +
                        " WHEN tracking_status = 'ON_TRACK' THEN 'EQUAL'" +
                        " WHEN tracking_status = 'AHEAD' THEN 'ABOVE'" +
                        " ELSE 'EQUAL'" +
                        " END)"
                )

                database.execSQL(
                    "UPDATE goal_period  SET tracking_status = (CASE " +
                        " WHEN tracking_status = 'BEHIND' THEN 'BELOW'" +
                        " WHEN tracking_status = 'ON_TRACK' THEN 'EQUAL'" +
                        " WHEN tracking_status = 'AHEAD' THEN 'ABOVE'" +
                        " ELSE 'EQUAL'" +
                        " END)"
                )

                database.execSQL("ALTER TABLE `account` ADD COLUMN `features` TEXT")
                database.execSQL("ALTER TABLE `account` ADD COLUMN `cdr_p_product_id` INTEGER")
                database.execSQL("ALTER TABLE `account` ADD COLUMN `cdr_p_product_name` TEXT")
                database.execSQL("ALTER TABLE `account` ADD COLUMN `cdr_p_product_details_page_url` TEXT")
                database.execSQL("ALTER TABLE `account` ADD COLUMN `cdr_p_key_information` TEXT")
                database.execSQL("ALTER TABLE `account` ADD COLUMN `products_available` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `provider` ADD COLUMN `products_available` INTEGER NOT NULL DEFAULT 0")

                database.execSQL("ALTER TABLE `transaction_model` ADD COLUMN `goal_id` INTEGER")

                database.execSQL("CREATE TABLE IF NOT EXISTS `image` (`image_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `image_types` TEXT NOT NULL, `small_image_url` TEXT NOT NULL, `large_image_url` TEXT NOT NULL, PRIMARY KEY(`image_id`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_image_image_id` ON `image` (`image_id`)")

                database.execSQL("CREATE TABLE IF NOT EXISTS `consent` (`consent_id` INTEGER NOT NULL, `provider_id` INTEGER NOT NULL, `provider_account_id` INTEGER, `permissions` TEXT NOT NULL, `additional_permissions` TEXT, `authorisation_request_url` TEXT, `confirmation_pdf_url` TEXT, `withdrawal_pdf_url` TEXT, `delete_redundant_data` INTEGER NOT NULL, `sharing_started_at` TEXT, `sharing_stopped_at` TEXT, `sharing_duration` INTEGER, `status` TEXT NOT NULL, PRIMARY KEY(`consent_id`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_consent_consent_id` ON `consent` (`consent_id`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_consent_provider_account_id` ON `consent` (`provider_account_id`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_consent_provider_id` ON `consent` (`provider_id`)")

                database.execSQL("CREATE TABLE IF NOT EXISTS `cdr_configuration` (`adr_id` TEXT NOT NULL, `adr_name` TEXT NOT NULL, `support_email` TEXT NOT NULL, `sharing_durations` TEXT NOT NULL, PRIMARY KEY(`adr_id`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_cdr_configuration_adr_id` ON `cdr_configuration` (`adr_id`)")

                // START - Drop column register_complete and add column register_steps
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("DROP INDEX IF EXISTS `index_user_user_id`")
                database.execSQL("ALTER TABLE user RENAME TO original_user")
                database.execSQL("CREATE TABLE IF NOT EXISTS `user` (`user_id` INTEGER NOT NULL, `first_name` TEXT, `email` TEXT NOT NULL, `email_verified` INTEGER NOT NULL, `status` TEXT NOT NULL, `primary_currency` TEXT NOT NULL, `valid_password` INTEGER NOT NULL, `register_steps` TEXT, `registration_date` TEXT NOT NULL, `facebook_id` TEXT, `attribution` TEXT, `last_name` TEXT, `mobile_number` TEXT, `gender` TEXT, `address` TEXT, `mailing_address` TEXT, `household_size` INTEGER, `marital_status` TEXT, `occupation` TEXT, `industry` TEXT, `date_of_birth` TEXT, `driver_license` TEXT, `features` TEXT, `foreign_tax` INTEGER, `tax_residency` TEXT, `tfn` TEXT, `tin` TEXT, PRIMARY KEY(`user_id`))")
                database.execSQL("INSERT INTO user(user_id, first_name, email, email_verified, status, primary_currency, valid_password, registration_date, facebook_id, attribution, last_name, mobile_number, gender, household_size, marital_status, occupation, industry, date_of_birth, driver_license, features) SELECT user_id, first_name, email, email_verified, status, primary_currency, valid_password, registration_date, facebook_id, attribution, last_name, mobile_number, gender, household_size, marital_status, occupation, industry, date_of_birth, driver_license, features FROM original_user")
                database.execSQL("DROP TABLE original_user")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_user_user_id` ON `user` (`user_id`)")
                database.execSQL("COMMIT")
                // END - Drop column register_complete and add column register_steps
            }
        }

        private val MIGRATION_10_11: Migration = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) New table - contact
                database.execSQL("CREATE TABLE IF NOT EXISTS `contact` (`contact_id` INTEGER NOT NULL, `created_date` TEXT NOT NULL, `modified_date` TEXT NOT NULL, `verified` INTEGER NOT NULL, `related_provider_account_ids` TEXT, `name` TEXT NOT NULL, `nick_name` TEXT NOT NULL, `description` TEXT, `payment_method` TEXT NOT NULL, `payment_details` TEXT, PRIMARY KEY(`contact_id`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_contact_contact_id` ON `contact` (`contact_id`)")
            }
        }

        private val MIGRATION_11_12: Migration = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) New table - card
                database.execSQL("CREATE TABLE IF NOT EXISTS `card` (`card_id` INTEGER NOT NULL, `account_id` INTEGER NOT NULL, `status` TEXT NOT NULL, `design_type` TEXT NOT NULL, `created_at` TEXT NOT NULL, `cancelled_at` TEXT, `name` TEXT, `nick_name` TEXT, `pan_last_digits` TEXT, `expiry_date` TEXT, `cardholder_name` TEXT, `type` TEXT, `issuer` TEXT, `pin_set_at` TEXT, PRIMARY KEY(`card_id`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_card_card_id` ON `card` (`card_id`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_card_account_id` ON `card` (`account_id`)")
            }
        }

        private val MIGRATION_12_13: Migration = object : Migration(12, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // New changes in this migration:
                // 1) New table - payday
                // 2) Alter cdr_configuration table - add column permissions
                // 3) Alter user table - add columns - external_id, residential_address_*, mailing_address_*, previous_address_* & drop columns - address, mailing_address
                // 4) New table - addresses

                database.execSQL("CREATE TABLE IF NOT EXISTS `payday` (`status` TEXT NOT NULL, `frequency` TEXT NOT NULL, `next_date` TEXT, `previous_date` TEXT, `payday_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")

                database.execSQL("ALTER TABLE `cdr_configuration` ADD COLUMN `permissions` TEXT")

                // START - Drop columns - address, mailing_address & add columns - external_id, residential_address_*, mailing_address_*, previous_address_*
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("DROP INDEX IF EXISTS `index_user_user_id`")
                database.execSQL("ALTER TABLE user RENAME TO original_user")
                database.execSQL("CREATE TABLE IF NOT EXISTS `user` (`user_id` INTEGER NOT NULL, `first_name` TEXT, `email` TEXT NOT NULL, `email_verified` INTEGER NOT NULL, `status` TEXT NOT NULL, `primary_currency` TEXT NOT NULL, `valid_password` INTEGER NOT NULL, `register_steps` TEXT, `registration_date` TEXT NOT NULL, `facebook_id` TEXT, `attribution` TEXT, `last_name` TEXT, `mobile_number` TEXT, `gender` TEXT, `household_size` INTEGER, `marital_status` TEXT, `occupation` TEXT, `industry` TEXT, `date_of_birth` TEXT, `driver_license` TEXT, `features` TEXT, `foreign_tax` INTEGER, `tax_residency` TEXT, `tfn` TEXT, `tin` TEXT, `external_id` TEXT, `residential_address_id` INTEGER, `residential_address_long_form` TEXT, `mailing_address_id` INTEGER, `mailing_address_long_form` TEXT, `previous_address_id` INTEGER, `previous_address_long_form` TEXT, PRIMARY KEY(`user_id`))")
                database.execSQL("INSERT INTO user(user_id, first_name, email, email_verified, status, primary_currency, valid_password, register_steps, registration_date, facebook_id, attribution, last_name, mobile_number, gender, household_size, marital_status, occupation, industry, date_of_birth, driver_license, features) SELECT user_id, first_name, email, email_verified, status, primary_currency, valid_password, register_steps, registration_date, facebook_id, attribution, last_name, mobile_number, gender, household_size, marital_status, occupation, industry, date_of_birth, driver_license, features FROM original_user")
                database.execSQL("DROP TABLE original_user")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_user_user_id` ON `user` (`user_id`)")
                database.execSQL("COMMIT")
                // END - Drop columns - address, mailing_address & add columns - external_id, residential_address_*, mailing_address_*, previous_address_*

                database.execSQL("CREATE TABLE IF NOT EXISTS `addresses` (`address_id` INTEGER NOT NULL, `building_name` TEXT, `unit_number` TEXT, `street_number` TEXT, `street_name` TEXT, `street_type` TEXT, `suburb` TEXT, `town` TEXT, `region` TEXT, `state` TEXT, `country` TEXT, `postal_code` TEXT, `long_form` TEXT, PRIMARY KEY(`address_id`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_addresses_address_id` ON `addresses` (`address_id`)")
            }
        }

        private val MIGRATION_13_14: Migration = object : Migration(13, 14) {
            override fun migrate(database: SupportSQLiteDatabase) {

                /** NOTE:
                 * There is one issue in this DB migration. By mistake the changes 1,2,3 below
                 * were made part of MIGRATION_12_13 for few 3.10.0 SDK builds and few Volt internal
                 * releases were also had this. Hence the only way now is - if a user migrates from
                 * 13 to 14 we have to Delete & Re-create 3 tables - account, transaction_model & cards
                 */

                // New changes in this migration:
                // 1) Delete & Re-create (NOT A STANDARD WAY. Only for this migration.) account table - To add new column payids
                // 2) Delete & Re-create (NOT A STANDARD WAY. Only for this migration.) transaction_model table - To add new column reference, reason
                // 3) Delete & Re-create (NOT A STANDARD WAY. Only for this migration.) cards table - To add digital wallet column

                // START - Delete & Re-create (NOT A STANDARD WAY. Only for this migration.) account table - To add new column payids
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("DROP TABLE `account`")
                database.execSQL("DROP INDEX IF EXISTS `index_account_account_id`")
                database.execSQL("DROP INDEX IF EXISTS `index_account_provider_account_id`")
                database.execSQL("CREATE TABLE IF NOT EXISTS `account` (`account_id` INTEGER NOT NULL, `account_name` TEXT NOT NULL, `account_number` TEXT, `bsb` TEXT, `nick_name` TEXT, `provider_account_id` INTEGER NOT NULL, `provider_name` TEXT NOT NULL, `aggregator` TEXT, `aggregator_id` INTEGER NOT NULL, `account_status` TEXT NOT NULL, `included` INTEGER NOT NULL, `favourite` INTEGER NOT NULL, `hidden` INTEGER NOT NULL, `apr` TEXT, `interest_rate` TEXT, `last_payment_date` TEXT, `due_date` TEXT, `end_date` TEXT, `goal_ids` TEXT, `external_id` TEXT NOT NULL, `features` TEXT, `products_available` INTEGER NOT NULL, `payids` TEXT, `h_profile_name` TEXT, `attr_account_type` TEXT NOT NULL, `attr_account_sub_type` TEXT NOT NULL, `attr_account_group` TEXT NOT NULL, `attr_account_classification` TEXT, `r_status_status` TEXT, `r_status_sub_status` TEXT, `r_status_additional_status` TEXT, `r_status_last_refreshed` TEXT, `r_status_next_refresh` TEXT, `c_balance_amount` TEXT, `c_balance_currency` TEXT, `a_balance_amount` TEXT, `a_balance_currency` TEXT, `a_cash_amount` TEXT, `a_cash_currency` TEXT, `a_credit_amount` TEXT, `a_credit_currency` TEXT, `t_cash_amount` TEXT, `t_cash_currency` TEXT, `t_credit_amount` TEXT, `t_credit_currency` TEXT, `int_totalamount` TEXT, `int_totalcurrency` TEXT, `a_due_amount` TEXT, `a_due_currency` TEXT, `m_amount_amount` TEXT, `m_amount_currency` TEXT, `l_payment_amount` TEXT, `l_payment_currency` TEXT, `b_details_current_description` TEXT, `b_details_tiers` TEXT, `cdr_p_product_id` INTEGER, `cdr_p_product_name` TEXT, `cdr_p_product_details_page_url` TEXT, `cdr_p_key_information` TEXT, PRIMARY KEY(`account_id`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_account_account_id` ON `account` (`account_id`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_account_provider_account_id` ON `account` (`provider_account_id`)")
                database.execSQL("COMMIT")
                // END - Delete & Re-create (NOT A STANDARD WAY. Only for this migration.) account table - To add new column payids

                // START - Delete & Re-create (NOT A STANDARD WAY. Only for this migration.) transaction_model table - To add new column reference, reason
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("DROP TABLE `transaction_model`")
                database.execSQL("DROP INDEX IF EXISTS `index_transaction_model_transaction_id`")
                database.execSQL("DROP INDEX IF EXISTS `index_transaction_model_account_id`")
                database.execSQL("DROP INDEX IF EXISTS `index_transaction_model_category_id`")
                database.execSQL("DROP INDEX IF EXISTS `index_transaction_model_merchant_id`")
                database.execSQL("CREATE TABLE IF NOT EXISTS `transaction_model` (`transaction_id` INTEGER NOT NULL, `base_type` TEXT NOT NULL, `status` TEXT NOT NULL, `transaction_date` TEXT NOT NULL, `post_date` TEXT, `budget_category` TEXT NOT NULL, `included` INTEGER NOT NULL, `memo` TEXT, `account_id` INTEGER NOT NULL, `category_id` INTEGER NOT NULL, `bill_id` INTEGER, `bill_payment_id` INTEGER, `user_tags` TEXT, `external_id` TEXT NOT NULL, `goal_id` INTEGER, `reference` TEXT, `reason` TEXT, `amount_amount` TEXT NOT NULL, `amount_currency` TEXT NOT NULL, `description_original` TEXT, `description_user` TEXT, `description_simple` TEXT, `merchant_id` INTEGER NOT NULL, `merchant_name` TEXT NOT NULL, `merchant_phone` TEXT, `merchant_website` TEXT, `merchant_location` TEXT, PRIMARY KEY(`transaction_id`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_model_transaction_id` ON `transaction_model` (`transaction_id`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_model_account_id` ON `transaction_model` (`account_id`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_model_category_id` ON `transaction_model` (`category_id`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_transaction_model_merchant_id` ON `transaction_model` (`merchant_id`)")
                database.execSQL("COMMIT")
                // END - Delete & Re-create (NOT A STANDARD WAY. Only for this migration.) transaction_model table - To add new column reference, reason

                // START - Delete & Re-create (NOT A STANDARD WAY. Only for this migration.) cards table - To add digital wallet column
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("DROP TABLE `card`")
                database.execSQL("DROP INDEX IF EXISTS `index_card_card_id`")
                database.execSQL("DROP INDEX IF EXISTS `index_card_account_id`")
                database.execSQL("CREATE TABLE IF NOT EXISTS `card` (`card_id` INTEGER NOT NULL, `account_id` INTEGER NOT NULL, `status` TEXT NOT NULL, `design_type` TEXT NOT NULL, `created_at` TEXT NOT NULL, `cancelled_at` TEXT, `name` TEXT, `nick_name` TEXT, `pan_last_digits` TEXT, `expiry_date` TEXT, `cardholder_name` TEXT, `type` TEXT, `issuer` TEXT, `digital_wallets` TEXT, `pin_set_at` TEXT, PRIMARY KEY(`card_id`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_card_card_id` ON `card` (`card_id`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_card_account_id` ON `card` (`account_id`)")
                database.execSQL("COMMIT")
                // END - Delete & Re-create (NOT A STANDARD WAY. Only for this migration.) cards table - To add digital wallet column
            }
        }

        private val MIGRATION_14_15: Migration = object : Migration(14, 15) {
            override fun migrate(database: SupportSQLiteDatabase) {

                // New changes in this migration:
                // 1) New table - service_outage
                // 2) Alter table - user - add tfn_status & foreign_tax_residency columns, delete tfn & tin columns

                database.execSQL("CREATE TABLE IF NOT EXISTS `service_outage` (`type` TEXT NOT NULL, `start_date` TEXT NOT NULL, `end_date` TEXT, `duration` INTEGER, `outage_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `read` INTEGER NOT NULL, `message_title` TEXT NOT NULL, `message_summary` TEXT NOT NULL, `message_description` TEXT NOT NULL, `message_action` TEXT NOT NULL, `message_url` TEXT NOT NULL)")

                // START - Drop column tfn, tin and add column foreign_tax_residency, tfn_status
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("DROP INDEX IF EXISTS `index_user_user_id`")
                database.execSQL("ALTER TABLE user RENAME TO original_user")
                database.execSQL("CREATE TABLE IF NOT EXISTS `user` (`user_id` INTEGER NOT NULL, `first_name` TEXT, `email` TEXT NOT NULL, `email_verified` INTEGER NOT NULL, `status` TEXT NOT NULL, `primary_currency` TEXT NOT NULL, `valid_password` INTEGER NOT NULL, `register_steps` TEXT, `registration_date` TEXT NOT NULL, `facebook_id` TEXT, `attribution` TEXT, `last_name` TEXT, `mobile_number` TEXT, `gender` TEXT, `household_size` INTEGER, `marital_status` TEXT, `occupation` TEXT, `industry` TEXT, `date_of_birth` TEXT, `driver_license` TEXT, `features` TEXT, `foreign_tax` INTEGER, `tax_residency` TEXT, `foreign_tax_residency` TEXT, `tfn_status` TEXT, `external_id` TEXT, `residential_address_id` INTEGER, `residential_address_long_form` TEXT, `mailing_address_id` INTEGER, `mailing_address_long_form` TEXT, `previous_address_id` INTEGER, `previous_address_long_form` TEXT, PRIMARY KEY(`user_id`))")
                database.execSQL("INSERT INTO user(user_id, first_name, email, email_verified, status, primary_currency, valid_password, register_steps, registration_date, facebook_id, attribution, last_name, mobile_number, gender, residential_address_id, residential_address_long_form, mailing_address_id, mailing_address_long_form, previous_address_id, previous_address_long_form, household_size, marital_status, occupation, industry, date_of_birth, driver_license, features, foreign_tax, tax_residency, external_id) SELECT user_id, first_name, email, email_verified, status, primary_currency, valid_password, register_steps, registration_date, facebook_id, attribution, last_name, mobile_number, gender, residential_address_id, residential_address_long_form, mailing_address_id, mailing_address_long_form, previous_address_id, previous_address_long_form, household_size, marital_status, occupation, industry, date_of_birth, driver_license, features, foreign_tax, tax_residency, external_id FROM original_user")
                database.execSQL("DROP TABLE original_user")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_user_user_id` ON `user` (`user_id`)")
                database.execSQL("COMMIT")
                // END - Drop column tfn, tin and add column foreign_tax_residency, tfn_status
            }
        }

        private val MIGRATION_15_16: Migration = object : Migration(15, 16) {
            override fun migrate(database: SupportSQLiteDatabase) {

                // New changes in this migration:
                // 1) Alter table - provider - add joint_accounts_available & associated_provider_ids columns
                // 2) Alter account table - Delete products_available column
                // 3) Drop & re-create table service_outage
                // 4) Drop & re-create table addresses

                database.execSQL("ALTER TABLE `provider` ADD COLUMN `joint_accounts_available` INTEGER")
                database.execSQL("ALTER TABLE `provider` ADD COLUMN `associated_provider_ids` TEXT")

                // START - Drop column products_available
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("DROP INDEX IF EXISTS `index_account_account_id`")
                database.execSQL("DROP INDEX IF EXISTS `index_account_provider_account_id`")
                database.execSQL("ALTER TABLE account RENAME TO original_account")
                database.execSQL("CREATE TABLE IF NOT EXISTS `account` (`account_id` INTEGER NOT NULL, `account_name` TEXT NOT NULL, `account_number` TEXT, `bsb` TEXT, `nick_name` TEXT, `provider_account_id` INTEGER NOT NULL, `provider_name` TEXT NOT NULL, `aggregator` TEXT, `aggregator_id` INTEGER NOT NULL, `account_status` TEXT NOT NULL, `included` INTEGER NOT NULL, `favourite` INTEGER NOT NULL, `hidden` INTEGER NOT NULL, `apr` TEXT, `interest_rate` TEXT, `last_payment_date` TEXT, `due_date` TEXT, `end_date` TEXT, `goal_ids` TEXT, `external_id` TEXT NOT NULL, `features` TEXT, `payids` TEXT, `h_profile_name` TEXT, `attr_account_type` TEXT NOT NULL, `attr_account_sub_type` TEXT NOT NULL, `attr_account_group` TEXT NOT NULL, `attr_account_classification` TEXT, `r_status_status` TEXT, `r_status_sub_status` TEXT, `r_status_additional_status` TEXT, `r_status_last_refreshed` TEXT, `r_status_next_refresh` TEXT, `c_balance_amount` TEXT, `c_balance_currency` TEXT, `a_balance_amount` TEXT, `a_balance_currency` TEXT, `a_cash_amount` TEXT, `a_cash_currency` TEXT, `a_credit_amount` TEXT, `a_credit_currency` TEXT, `t_cash_amount` TEXT, `t_cash_currency` TEXT, `t_credit_amount` TEXT, `t_credit_currency` TEXT, `int_totalamount` TEXT, `int_totalcurrency` TEXT, `a_due_amount` TEXT, `a_due_currency` TEXT, `m_amount_amount` TEXT, `m_amount_currency` TEXT, `l_payment_amount` TEXT, `l_payment_currency` TEXT, `b_details_current_description` TEXT, `b_details_tiers` TEXT, `cdr_p_product_id` INTEGER, `cdr_p_product_name` TEXT, `cdr_p_product_details_page_url` TEXT, `cdr_p_key_information` TEXT, PRIMARY KEY(`account_id`))")
                database.execSQL("INSERT INTO account(account_id, account_name, account_number, bsb, nick_name, provider_account_id, provider_name, aggregator, aggregator_id, account_status, included, favourite, hidden, apr, interest_rate, last_payment_date, due_date, end_date, goal_ids, external_id, features, payids, h_profile_name, attr_account_type, attr_account_sub_type, attr_account_group, attr_account_classification, r_status_status, r_status_sub_status, r_status_last_refreshed, r_status_next_refresh, c_balance_amount, c_balance_currency, a_balance_amount, a_balance_currency, a_cash_amount, a_cash_currency, a_credit_amount, a_credit_currency, t_cash_amount, t_cash_currency, t_credit_amount, t_credit_currency, int_totalamount, int_totalcurrency, a_due_amount, a_due_currency, m_amount_amount, m_amount_currency, l_payment_amount, l_payment_currency, b_details_current_description, b_details_tiers, cdr_p_product_id, cdr_p_product_name, cdr_p_product_details_page_url, cdr_p_key_information) SELECT account_id, account_name, account_number, bsb, nick_name, provider_account_id, provider_name, aggregator, aggregator_id, account_status, included, favourite, hidden, apr, interest_rate, last_payment_date, due_date, end_date, goal_ids, external_id, features, payids, h_profile_name, attr_account_type, attr_account_sub_type, attr_account_group, attr_account_classification, r_status_status, r_status_sub_status, r_status_last_refreshed, r_status_next_refresh, c_balance_amount, c_balance_currency, a_balance_amount, a_balance_currency, a_cash_amount, a_cash_currency, a_credit_amount, a_credit_currency, t_cash_amount, t_cash_currency, t_credit_amount, t_credit_currency, int_totalamount, int_totalcurrency, a_due_amount, a_due_currency, m_amount_amount, m_amount_currency, l_payment_amount, l_payment_currency, b_details_current_description, b_details_tiers, cdr_p_product_id, cdr_p_product_name, cdr_p_product_details_page_url, cdr_p_key_information FROM original_account")
                database.execSQL("DROP TABLE original_account")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_account_account_id` ON `account` (`account_id`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_account_provider_account_id` ON `account` (`provider_account_id`)")
                database.execSQL("COMMIT")
                // END - Drop column products_available

                // START - Drop & re-create table service_outage
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("DROP TABLE service_outage")
                database.execSQL("CREATE TABLE IF NOT EXISTS `service_outage` (`type` TEXT NOT NULL, `start_date` TEXT NOT NULL, `end_date` TEXT, `duration` INTEGER, `outage_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `read` INTEGER NOT NULL, `message_title` TEXT NOT NULL, `message_summary` TEXT NOT NULL, `message_description` TEXT NOT NULL, `message_action` TEXT NOT NULL, `message_url` TEXT)")
                database.execSQL("COMMIT")
                // END - Drop & re-create table service_outage

                // START - Drop & re-create table addresses
                database.execSQL("BEGIN TRANSACTION")
                database.execSQL("DROP INDEX IF EXISTS `index_addresses_address_id`")
                database.execSQL("DROP TABLE addresses")
                database.execSQL("CREATE TABLE IF NOT EXISTS `addresses` (`address_id` INTEGER NOT NULL, `dpid` TEXT, `line_1` TEXT, `line_2` TEXT, `suburb` TEXT, `town` TEXT, `region` TEXT, `state` TEXT, `country` TEXT, `postal_code` TEXT, `long_form` TEXT, PRIMARY KEY(`address_id`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_addresses_address_id` ON `addresses` (`address_id`)")
                database.execSQL("COMMIT")
                // END - Drop & re-create table addresses
            }
        }
    }
}
