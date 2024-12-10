import React, { useState } from "react";
import Link from "next/link";
import Image from "next/image";
import ProfileCard from "@/components/leftmenu/profile-card";

const LeftMenu = ({ type, onFilterChange }) => {
  // State for sorting and filtering options
  const [sortOption, setSortOption] = useState("new");
  const [filterOption, setFilterOption] = useState({
    beforeDate: "",
    afterDate: "",
    followingOnly: false,
  });

  const handleSortChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
  	const newSortOption = e.target.value;
    setSortOption(newSortOption);
    onFilterChange({sortBy: newSortOption, filterOption})
  };

  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    const newFilterOption = {
      ...filterOption,
      [name]: type === "checkbox" ? checked : value,
    };
    setFilterOption(newFilterOption);
    onFilterChange({ sortBy: sortOption, filterOption: newFilterOption });
  };

  return (
    <div className="flex flex-col gap-4 sticky top-10">
      {type === "home" && <ProfileCard />}

      <div className="p-4 bg-slate-600 rounded-lg shadow-md text-sm text-gray-500 flex flex-col h-auto">
        {/* Sorting Options */}
        <div className="font-bold text-center text-3xl text-slate-100 mr-2 mb-2">
          Sort
        </div>
        <div className="flex items-center justify-end">
          <label htmlFor="sortOption" className="text-gray-300 mr-2">
            Sort By:
          </label>
          <select
            id="sortOption"
            value={sortOption}
            onChange={handleSortChange}
            className="bg-slate-500 text-white rounded-md px-2 py-1 transition-colors duration-100 hover:bg-slate-700"
          >
            <option value="new">New</option>
            <option value="popularity">Popularity</option>
          </select>
        </div>

        {/* Filtering Options */}
        <div className="font-bold text-center text-3xl text-slate-100 mr-2 mt-6 mb-2">
          Filter
        </div>
        <div className="flex flex-col gap-2">
          <div className="flex items-center justify-end">
            <label htmlFor="beforeDate" className="text-gray-300 mr-2">
              Before Date:
            </label>
            <input
              type="date"
              id="beforeDate"
              name="beforeDate"
              value={filterOption.beforeDate}
              onChange={handleFilterChange}
              className="bg-slate-500 text-white rounded-md px-2 py-1 transition-colors duration-100 hover:bg-slate-700"
            />
          </div>

          <div className="flex items-center justify-end">
            <label htmlFor="afterDate" className="text-gray-300 mr-2">
              After Date:
            </label>
            <input
              type="date"
              id="afterDate"
              name="afterDate"
              value={filterOption.afterDate}
              onChange={handleFilterChange}
              className="bg-slate-500 text-white rounded-md px-2 py-1 transition-colors duration-100 hover:bg-slate-700"
            />
          </div>

          <div className="flex items-center justify-end">
            <input
              type="checkbox"
              id="followingOnly"
              name="followingOnly"
              checked={filterOption.followingOnly}
              onChange={handleFilterChange}
              className="mr-2"
            />
            <label htmlFor="followingOnly" className="text-gray-300 hover:text-slate-400">
              Following Only
            </label>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LeftMenu;
