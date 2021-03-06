import React from "react";
import {Link} from "react-router-dom";
import * as PROP from "../properties";
import axios from "axios";
import {Header} from "./Header";

export class RepositoryFiles extends React.Component {

  state = {
    repoId: 0,
    bases: [],
    solutions: [],
    repoName: "",
    sortedByName: "",
    baseSortMethod: (a, b) => 0,
    solutionSortMethod: (a, b) => 0
  };

  constructor(props, context) {
    super(props, context);
    this.state.repoId = props.match.params.id;
    axios.get(PROP.serverUrl + "/api/repositories/" + this.state.repoId).then((response) => {
      this.setState({repoName: response.data.name});
    });
    this.fetchFiles();
    this.handleChange = this.handleChange.bind(this);
    this.fetchFiles = this.fetchFiles.bind(this);
    this.deleteBaseFiles = this.deleteBaseFiles.bind(this);
    this.deleteSolutionFiles = this.deleteSolutionFiles.bind(this);
  }

  static fileElementBase(base, file) {
    return {id: file.id, branch: base.branch, updated: base.updated.toString().replace("T", " "), name: file.name};
  }

  static fileElementSolution(solution, student, file) {
    return {
      id: file.id,
      branch: solution.sourceBranch,
      student: student.student,
      updated: student.updated.replace("T", " "),
      name: file.name
    };
  }

  static filterBase(regStr, base) {
    try {
      let regexp = new RegExp(regStr);
      return regexp.test(base.name.toLowerCase()) || regexp.test(base.branch.toLowerCase()) || regexp.test(base.updated.toLowerCase());
    } catch (e) {
      return true;
    }
  }

  static baseElement(baseBranch) {
    return baseBranch.files.map((file) => RepositoryFiles.fileElementBase(baseBranch, file));
  }

  static solutionElement(solutionBranch) {
    return solutionBranch.solutions.flatMap((solution) => RepositoryFiles.solutionUserElement(solutionBranch, solution));
  }

  static solutionUserElement(solutionBranch, solution) {
    return solution.files.map((file) => RepositoryFiles.fileElementSolution(solutionBranch, solution, file));
  }

  deleteBaseFiles() {
    axios.delete(PROP.serverUrl + "/api/repositories/" + this.state.repoId + "/bases/delete").then(
      () => this.fetchFiles()
    )
  }

  deleteSolutionFiles() {
    axios.delete(PROP.serverUrl + "/api/repositories/" + this.state.repoId + "/solutions/delete").then(
      () => this.fetchFiles()
    );
  }

  handleChange(event) {
    this.setState({
      [event.target.name]: event.target.value.toLowerCase()
    });
  }

  static filterSolution(regStr, sol) {
    try {
      let regexp = new RegExp(regStr);
      return regexp.test(sol.name.toLowerCase()) || regexp.test(sol.branch.toLowerCase()) || regexp.test(sol.updated.toLowerCase()) || regexp.test(sol.student.toLowerCase());
    } catch (e) {
      return true;
    }
  }

  fetchFiles() {
    axios.get(PROP.serverUrl + "/api/repositories/" + this.state.repoId + "/files").then((response) => {
      let bases = response.data.baseBranches.flatMap((baseBranch) => RepositoryFiles.baseElement(baseBranch));
      let solutions = response.data.solutionBranches.flatMap((solutionBranch) => RepositoryFiles.solutionElement(solutionBranch));
      this.setState({
        bases, solutions
      });
    });
  }

  sortByName = (a, b) => a.name.localeCompare(b.name);
  sortByStudent = (a, b) => a.student.localeCompare(b.student);
  sortByBranch = (a, b) => a.branch.localeCompare(b.branch);
  sortByUpdated = (a, b) => a.updated.localeCompare(b.updated);

  changeBaseSortMethodTo(method) {
    if (this.state.baseSortMethod === method) {
      this.setState({baseSortMethod: (a, b) => method(a, b) * -1});
    } else {
      this.setState({baseSortMethod: method});
    }
  }

  changeSolutionSortMethodTo(method) {
    if (this.state.solutionSortMethod === method) {
      this.setState({solutionSortMethod: (a, b) => method(a, b) * -1})
    } else {
      this.setState({solutionSortMethod: method});
    }
  }

  render() {
    return (<div className="container">
      {new Header(
        <ol className="breadcrumb">
          <li className="breadcrumb-item"><Link to="/repos">Repositories</Link></li>
          <li className="breadcrumb-item"><Link to={"/repos/" + this.state.repoId}>{this.state.repoName}</Link></li>
          <li className="breadcrumb-item"><Link onClick={() => this.fetchFiles()}
                                                to={"/repos/" + this.state.repoId + "/files"}>Files</Link></li>
        </ol>)}
      <div className="row">
        <div className="col container">
          <div className="col">
            <div className="row mb-2">
              <div className="col">
                <div className="row">
                  <h5>Downloaded files of repository {this.state.repoName}</h5>
                </div>
              </div>
            </div>
            <div className="row">
              <h6>Base files</h6>
              <button className="badge badge-danger mb-2 ml-2" onClick={() => this.deleteBaseFiles()}>Delete all
              </button>
              <table className="table table-hover file-table table-sm">
                <thead className="thead-light">
                <tr>
                  <th className="break-word" onClick={() => this.changeBaseSortMethodTo(this.sortByName)}>Name</th>
                  <th onClick={() => this.changeBaseSortMethodTo(this.sortByBranch)}>Branch</th>
                  <th onClick={() => this.changeBaseSortMethodTo(this.sortByUpdated)}>Updated</th>
                </tr>
                </thead>
                <tbody>
                {this.state.bases.filter(
                  (it) => RepositoryFiles.filterBase(this.state.sortedByName, it)
                ).sort(this.state.baseSortMethod).map((base) => <tr>
                  <td className="break-word">{base.name}</td>
                  <td className="break-word">{base.branch}</td>
                  <td className="th-lg text-nowrap">{base.updated}</td>
                </tr>)}
                </tbody>
              </table>
            </div>
          </div>
        </div>
        <div className="container col-sm-3">
          <div className="list-group input-group">
            <input name="sortedByName" onChange={this.handleChange} autoComplete="off"
                   className="list-group-item list-group-item-action text-input" placeholder="Search by regex"/>
          </div>
        </div>
      </div>
      <div className="row">
        <div className="col container">
          <div className="row ml-0">
            <h6>Solution files</h6>
            <button className="badge badge-danger mb-2 ml-2" onClick={() => this.deleteSolutionFiles()}>Delete all
            </button>
          </div>
          <table className="table table-hover file-table table-sm">
            <thead className="thead-light">
            <tr>
              <th className="break-word" onClick={() => this.changeSolutionSortMethodTo(this.sortByStudent)}>Student
              </th>
              <th className="break-word" onClick={() => this.changeSolutionSortMethodTo(this.sortByName)}>Name</th>
              <th className="break-word" onClick={() => this.changeSolutionSortMethodTo(this.sortByBranch)}>Branch</th>
              <th className="th-lg text-nowrap"
                  onClick={() => this.changeSolutionSortMethodTo(this.sortByUpdated)}>Updated
              </th>
            </tr>
            </thead>
            <tbody>
            {this.state.solutions.filter(
              (it) => RepositoryFiles.filterSolution(this.state.sortedByName, it)
            ).sort(this.state.solutionSortMethod).map((base) => <tr>
              <td>{base.student}</td>
              <td className="break-word">{base.name}</td>
              <td className="break-word">{base.branch}</td>
              <td className="th-lg text-nowrap">{base.updated}</td>
            </tr>)}
            </tbody>
          </table>
        </div>
      </div>
    </div>);
  }
}