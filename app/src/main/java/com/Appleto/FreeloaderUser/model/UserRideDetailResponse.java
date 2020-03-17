package com.Appleto.FreeloaderUser.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserRideDetailResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public class Datum {

        @SerializedName("ride_request_id")
        @Expose
        private String rideRequestId;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("source_address")
        @Expose
        private String sourceAddress;
        @SerializedName("destination_address")
        @Expose
        private String destinationAddress;
        @SerializedName("source_lat")
        @Expose
        private String pickup_location_lat;
        @SerializedName("source_long")
        @Expose
        private String pickup_location_long;
        @SerializedName("destination_lat")
        @Expose
        private String destinationLat;
        @SerializedName("destination_long")
        @Expose
        private String destinationLong;
        @SerializedName("riders")
        @Expose
        private String riders;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("accepted_driver_id")
        @Expose
        private String acceptedDriverId;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("driver_name")
        @Expose
        private String driverName;
        @SerializedName("driver_phone")
        @Expose
        private String driverPhone;
        @SerializedName("driver_lat")
        @Expose
        private String driverLat;
        @SerializedName("driver_long")
        @Expose
        private String driverLong;

        public String getRideRequestId() {
            return rideRequestId;
        }

        public void setRideRequestId(String rideRequestId) {
            this.rideRequestId = rideRequestId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSourceAddress() {
            return sourceAddress;
        }

        public void setSourceAddress(String sourceAddress) {
            this.sourceAddress = sourceAddress;
        }

        public String getDestinationAddress() {
            return destinationAddress;
        }

        public void setDestinationAddress(String destinationAddress) {
            this.destinationAddress = destinationAddress;
        }

        public String getSourceLat() {
            return pickup_location_lat;
        }

        public void setSourceLat(String pickup_location_lat) {
            this.pickup_location_lat = pickup_location_lat;
        }

        public String getSourceLong() {
            return pickup_location_long;
        }

        public void setSourceLong(String pickup_location_long) {
            this.pickup_location_long = pickup_location_long;
        }

        public String getDestinationLat() {
            return destinationLat;
        }

        public void setDestinationLat(String destinationLat) {
            this.destinationLat = destinationLat;
        }

        public String getDestinationLong() {
            return destinationLong;
        }

        public void setDestinationLong(String destinationLong) {
            this.destinationLong = destinationLong;
        }

        public String getRiders() {
            return riders;
        }

        public void setRiders(String riders) {
            this.riders = riders;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAcceptedDriverId() {
            return acceptedDriverId;
        }

        public void setAcceptedDriverId(String acceptedDriverId) {
            this.acceptedDriverId = acceptedDriverId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDriverName() {
            return driverName;
        }

        public void setDriverName(String driverName) {
            this.driverName = driverName;
        }

        public String getDriverPhone() {
            return driverPhone;
        }

        public void setDriverPhone(String driverPhone) {
            this.driverPhone = driverPhone;
        }

        public String getDriverLat() {
            return driverLat;
        }

        public void setDriverLat(String driverLat) {
            this.driverLat = driverLat;
        }

        public String getDriverLong() {
            return driverLong;
        }

        public void setDriverLong(String driverLong) {
            this.driverLong = driverLong;
        }

    }
}
