require 'test_helper'

class ExptsControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:expts)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create expt" do
    assert_difference('Expt.count') do
      post :create, :expt => { }
    end

    assert_redirected_to expt_path(assigns(:expt))
  end

  test "should show expt" do
    get :show, :id => expts(:one).id
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => expts(:one).id
    assert_response :success
  end

  test "should update expt" do
    put :update, :id => expts(:one).id, :expt => { }
    assert_redirected_to expt_path(assigns(:expt))
  end

  test "should destroy expt" do
    assert_difference('Expt.count', -1) do
      delete :destroy, :id => expts(:one).id
    end

    assert_redirected_to expts_path
  end
end
