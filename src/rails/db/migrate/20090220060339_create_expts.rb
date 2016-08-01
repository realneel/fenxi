class CreateExpts < ActiveRecord::Migration
  def self.up
    create_table :expts do |t|

      t.timestamps
    end
  end

  def self.down
    drop_table :expts
  end
end
