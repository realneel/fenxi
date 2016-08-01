class CreateCompares < ActiveRecord::Migration
  def self.up
    create_table :compares do |t|

      t.timestamps
    end
  end

  def self.down
    drop_table :compares
  end
end
