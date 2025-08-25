const mongoose = require('mongoose');
const bcrypt = require('bcrypt');
require('dotenv').config();

// Connect to MongoDB
const connectDB = async () => {
    try {
        await mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/bookstore', {
            useNewUrlParser: true,
            useUnifiedTopology: true,
        });
        console.log('MongoDB connected successfully');
    } catch (error) {
        console.error('MongoDB connection error:', error);
        process.exit(1);
    }
};

// Migration function
const migrateCustomers = async () => {
    try {
        console.log('Starting customer migration...');
        
        // Get all customers
        const customers = await mongoose.connection.db.collection('customers').find({}).toArray();
        console.log(`Found ${customers.length} customers to migrate`);
        
        let migratedCount = 0;
        const defaultPassword = 'password123'; // Default password for existing customers
        const saltRounds = 10;
        const hashedPassword = await bcrypt.hash(defaultPassword, saltRounds);
        
        for (const customer of customers) {
            const updateFields = {};
            const unsetFields = {};
            
            // Add password if missing
            if (!customer.password) {
                updateFields.password = hashedPassword;
            }
            
            // Add fullName if missing
            if (!customer.fullName && customer.firstName && customer.lastName) {
                updateFields.fullName = `${customer.firstName} ${customer.lastName}`;
            }
            
            // Remove deprecated fields
            if (customer.address) {
                unsetFields.address = '';
            }
            if (customer.browsingHistory) {
                unsetFields.browsingHistory = '';
            }
            if (customer.hasOwnProperty('isActive')) {
                unsetFields.isActive = '';
            }
            if (customer.preferences && customer.preferences.notifications) {
                unsetFields['preferences.notifications'] = '';
            }
            
            // Perform update if there are changes
            if (Object.keys(updateFields).length > 0 || Object.keys(unsetFields).length > 0) {
                const updateQuery = {};
                
                if (Object.keys(updateFields).length > 0) {
                    updateQuery.$set = updateFields;
                }
                
                if (Object.keys(unsetFields).length > 0) {
                    updateQuery.$unset = unsetFields;
                }
                
                await mongoose.connection.db.collection('customers').updateOne(
                    { _id: customer._id },
                    updateQuery
                );
                
                migratedCount++;
                console.log(`Migrated customer: ${customer.email}`);
            }
        }
        
        console.log(`\nMigration completed successfully!`);
        console.log(`Total customers migrated: ${migratedCount}`);
        console.log(`Default password for existing customers: ${defaultPassword}`);
        console.log('\nRemoved fields:');
        console.log('- address');
        console.log('- browsingHistory');
        console.log('- isActive');
        console.log('- preferences.notifications');
        console.log('\nAdded fields:');
        console.log('- password (hashed)');
        console.log('- fullName (generated from firstName + lastName)');
        
    } catch (error) {
        console.error('Migration error:', error);
        throw error;
    }
};

// Run migration
const runMigration = async () => {
    try {
        await connectDB();
        await migrateCustomers();
        console.log('\nMigration completed. Closing database connection...');
        await mongoose.connection.close();
        process.exit(0);
    } catch (error) {
        console.error('Migration failed:', error);
        process.exit(1);
    }
};

// Execute if run directly
if (require.main === module) {
    runMigration();
}

module.exports = { migrateCustomers };